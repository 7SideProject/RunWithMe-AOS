package com.side.runwithme.view.running

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.side.runwithme.R
import com.side.runwithme.base.BaseActivity
import com.side.runwithme.databinding.ActivityRunningBinding
import com.side.runwithme.mapper.mapperToCoordinateList
import com.side.runwithme.model.CoordinatesParcelable
import com.side.runwithme.model.RunRecordParcelable
import com.side.runwithme.service.RunningService
import com.side.runwithme.service.SERVICE_NOTSTART
import com.side.runwithme.util.GOAL_TYPE
import com.side.runwithme.util.LOCATION_PERMISSION_REQUEST_CODE
import com.side.runwithme.util.RUNNING_STATE
import com.side.runwithme.util.TrackingUtility
import com.side.runwithme.util.onlyTimeFormatter
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.view.MainActivity
import com.side.runwithme.view.loading.LoadingDialog
import com.side.runwithme.view.running_result.RunningResultActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class RunningActivity : BaseActivity<ActivityRunningBinding>(R.layout.activity_running),
    OnMapReadyCallback {

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    private val runningViewModel: RunningViewModel by viewModels<RunningViewModel>()

    private lateinit var polyline: PathOverlay

    private lateinit var locationSource: FusedLocationSource
    private var naverMap: NaverMap? = null

    private var naverLatLng = listOf<LatLng>()

    private lateinit var runningService: RunningService

    // 라이브 데이터 받아온 값들
    private var caloriesBurned: Int = 0
    private var sumDistance: Float = 0f
    private var currentTimeInMillis = 0L

    private var imgFile: MultipartBody.Part? = null
    private var imgByteArray: ByteArray? = null
    private var coordinates: ArrayList<CoordinatesParcelable> = arrayListOf()
    private lateinit var runRecord: RunRecordParcelable
    private var isStopError = false

    private lateinit var loadingDialog: LoadingDialog

    private val FOUR_HOURS_IN_MILLIS = 4 * 60 * 60 * 1000

    override fun init() {
        val intent = intent
        val challengeSeq = intent.getLongExtra("challengeSeq", 0L)
        val type = intent.getIntExtra("goalType", -1)
        val goal = intent.getLongExtra("goalAmount", 0L)
        val challengeName = intent.getStringExtra("challengeName") ?: ""

        initMapView()

        initClickListener()

        // onBackPressed deprecated되고 아래처럼 사용해야함
        this.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        if (RunningService.serviceState == SERVICE_NOTSTART) {

            if(challengeSeq == 0L || type == -1 || goal == 0L || challengeName.isEmpty()){
                startError(resources.getString(R.string.running_error_not_found))
                finish()
            }

            runningViewModel.saveChallengeInfo(challengeSeq, type, goal, challengeName)

            // 연습 모드는 -1, 나머지 challenge는 1이상이 들어와야함
            require(runningViewModel.challengeSeq.value != 0L)

            firstStart()
        } else { // app killed 된 후 activity 재시작
            bindService()
        }

        initRunningInfo()

        initDrawLine()

        initViewModelCallback()

    }

    private fun initRunningInfo(){
        runningViewModel.apply {
            getMyWeight()
            getChallnegeInfo()
        }
    }

    private fun startError(message: String){
        showToast(message)
        startActivity(Intent(this@RunningActivity, MainActivity::class.java))
        finish()
    }

    private fun initViewModelCallback() {
        repeatOnStarted {
            runningViewModel.postRunRecordEventFlow.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun bindService() {
        // bindService
        Intent(this@RunningActivity, RunningService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun handleEvent(event: RunningViewModel.Event) {
        when (event) {
            is RunningViewModel.Event.Success -> {
                runningViewModel.saveChallengeInfo(0, -1, 0L, "")
                loadingDialog.dismiss()
                // 서버에 등록이 완료된 후 service를 종료 시킴
                // 서버에 등록하기 전에 acitivity가 파괴되면 기록을 잃을 우려
                stopService()

                val intent = Intent(this, RunningResultActivity::class.java).apply {
                    putExtra("runRecord", runRecord)
                    putParcelableArrayListExtra("coordinates", coordinates)
//                    putExtra("challengeSeq", runningViewModel.challengeSeq.value)
                    putExtra("challengeName", runningViewModel.challengeName.value)
                    putExtra("imgByteArray", imgByteArray)
                }

                startActivity(intent)
                finish()
            }
            is RunningViewModel.Event.NotOver15Seconds -> {
                runningViewModel.saveChallengeInfo(0, -1, 0L, "")
                loadingDialog.dismiss()
                // 서버에 등록이 완료된 후 service를 종료 시킴
                // 서버에 등록하기 전에 acitivity가 파괴되면 기록을 잃을 우려
                stopService()

                showToast("15초 이하의 기록은 저장되지 않습니다.")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            is RunningViewModel.Event.Fail -> {
                showToast("다시 한 번 시도해주세요.")
            }
            is RunningViewModel.Event.ServerError -> {
                showToast("서버가 불안정합니다. 다시 한 번 시도해주세요.")
            }
            is RunningViewModel.Event.Error -> {

            }
            is RunningViewModel.Event.GetDataStoreValuesError -> {
                val isOver1Minute = currentTimeInMillis > 60000L
                if(isOver1Minute) { // 1분
                    startError(resources.getString(R.string.running_error_not_found))
                }else{
                    runningViewModel.getChallnegeInfo()
                }
            }
        }
    }

    private fun initClickListener() {
        binding.apply {
            ibStart.setOnClickListener {
                runningBtnUI()

                sendCommandToService(RUNNING_STATE.RESUME.name)
            }

            ibPause.setOnClickListener {
                pauseBtnUI()

                sendCommandToService(RUNNING_STATE.PAUSE.name)
            }

            ibStop.setOnClickListener {
                ibStart.visibility = View.GONE
                ibPause.visibility = View.GONE
                stopRun()
            }
        }
    }

    private fun runningBtnUI() {
        binding.apply {
            ibStart.visibility = View.GONE
            ibStop.visibility = View.GONE
            ibPause.visibility = View.VISIBLE
        }
    }

    private fun pauseBtnUI() {
        binding.apply {
            ibStart.visibility = View.VISIBLE
            ibStop.visibility = View.VISIBLE
            ibPause.visibility = View.GONE
        }
    }



    private fun initMapView() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_view, it).commit()
            }
        mapFragment.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun initDrawLine() {
        polyline = PathOverlay()
    }

    private fun initObserve() {

        // 좌표 observe
        runningService.pathPoints.observe(this) {
            if (it.isNotEmpty()) {
                naverLatLng = it.mapperToListNaverLatLng()

                val isOkToDrawPolyline = it.size >= 2
                if (isOkToDrawPolyline && naverMap != null) {
                    drawPolyline()
                    moveMyLocation()
                    naverMap?.moveCamera(CameraUpdate.zoomTo(16.0))
                }
            }
        }

        // 시간(타이머) 경과 observe
        runningService.timeRunInMillis.observe(this) {
            currentTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTimeSummery(it)
            changeTimeText(formattedTime)

            // 프로그래스바 진행도 변경
            if (it > 0 && runningViewModel.goalType.value == GOAL_TYPE.TIME) {
                binding.progressBarGoal.progress =
                    if ((it / (runningViewModel.goalAmount.value / 100)).toInt() >= 100) 100 else (it / (runningViewModel.goalAmount.value / 100)).toInt()
            }

            val startTime = runningService.startTime
            val endTime = System.currentTimeMillis()

            val isRunningAvailableUntilFourHour = (endTime - startTime) >= FOUR_HOURS_IN_MILLIS
            if(isRunningAvailableUntilFourHour){
                stopRun()
            }
        }

        // 거리 observe
        runningService.sumDistance.observe(this) {
            sumDistance = it
            changeDistanceText(sumDistance)
            changeCalorie(sumDistance)

            // 프로그래스바 진행도 변경
            if (sumDistance > 0 && runningViewModel.goalType.value == GOAL_TYPE.DISTANCE) {
                binding.progressBarGoal.progress =
                    if ((sumDistance / (runningViewModel.goalAmount.value / 100)).toInt() >= 100) 100 else (sumDistance / (runningViewModel.goalAmount.value / 100)).toInt()
            }
        }

        // 러닝 뛰고 있는 지 observe
        runningService.isRunning.observe(this) {
            if (it) { // 러닝을 뛰고 있는 경우
                runningBtnUI()
            } else { // 일시 정지 된 경우
                pauseBtnUI()
            }
        }

        // Tracking을 하지 못해 에러 발생
        runningService.errorEvent.observe(this) {
            if(it){
                startError(resources.getString(R.string.not_supported_location))
                finish()
            }
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions,
                grantResults
            )
        ) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap?.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun firstStart() {

        val runningLoadingDialog = RunningLoadingDialog(this)
        runningLoadingDialog.show()

        lifecycleScope.launch {
            startRun()
            runningLoadingDialog.dismiss()
        }

    }

    private suspend fun startRun() {
        sendCommandToService(RUNNING_STATE.FIRST_SHOW.name)
        delay(3000L)

        sendCommandToService(RUNNING_STATE.START.name)

        bindService()

        delay(1000L)
    }

    // 달리기 종료
    private fun stopRun() {
        lifecycleScope.launch {
            loading(2000L)

        }

        lifecycleScope.launch{
            if (!isStopError) { // 서버 에러 등으로 다시 stop을 눌러야할 때 한 번 더 저장 안하도록, (bitmap 하나 더 생성하기 때문에 메모리 누수 우려)
                endToSaveData()
                isStopError = true
            }

            if(isStopError and (imgFile == null)){
                endToSaveData()
            }

            runningService.stopRunningBeforeRegister = true

            if(runningViewModel.challengeSeq.value == -1L){ // 연습러닝
                runningViewModel.postPracticeRunRecord(runRecord, imgByteArray)
            }else {
                runningViewModel.postChallengeRunRecord(
                    runRecord = runRecord, coordinates = coordinates, image = imgFile,
                )
            }
        }

    }

    private suspend fun endToSaveData() {
        takeSnapShot()
        changeCoordinates()
        saveRunRecord()
        delay(500L)
    }

    private fun saveRunRecord() {
        var successYN = "N"
        /** goalAmount하고 sumDistance, runningTime의 단위 맞춰야함 **/
        if(runningViewModel.goalType.value == GOAL_TYPE.DISTANCE && runningViewModel.goalAmount.value <= sumDistance){
            successYN = "Y"
        }

        val runningTime = (currentTimeInMillis / 1000).toInt()

        if(runningViewModel.goalType.value == GOAL_TYPE.TIME && runningViewModel.goalAmount.value <= runningTime){
            successYN = "Y"
        }

        runRecord = RunRecordParcelable(
            runRecordSeq = 0,
            startTime = onlyTimeFormatter(runningService.startTime),
            endTime = onlyTimeFormatter(System.currentTimeMillis()),
            runningDay = runningService.startDay,
            runningTime = runningTime,
            runningDistance = sumDistance.toInt(),
            avgSpeed = 1.0 * (sumDistance / 1000f) / (1.0F * runningTime / 3600),
            calorie = caloriesBurned,
            successYN = successYN
        )
    }

    private fun changeCoordinates() {
        coordinates = naverLatLng.mapperToCoordinateList()
    }

    private fun takeSnapShot() {
        moveLatLngBounds()

        lifecycleScope.launch {
            delay(300L)

            naverMap?.takeSnapshot {
                // image 생성
                imgByteArray = createByteArray(it)
                imgFile = createMultiPart(imgByteArray)
            }
        }
    }

    private fun createByteArray(bitmap: Bitmap): ByteArray{
        val outputStream = ByteArrayOutputStream()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 10, outputStream)
        }else{
            bitmap.compress(Bitmap.CompressFormat.WEBP, 10, outputStream)
        }
        return outputStream.toByteArray()
    }

    private fun createMultiPart(imageByteArray: ByteArray?): MultipartBody.Part? {
        if(imageByteArray == null){
            return null
        }

        val requestFile = imageByteArray.toRequestBody("image/webp".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", "running", requestFile)
    }


    // 이동한 전체 polyLine 담기
    private fun moveLatLngBounds() {
        if(naverLatLng.isEmpty()) return

        val latLngBoundsBuilder = LatLngBounds.Builder().include(naverLatLng)
        val bounds = latLngBoundsBuilder.build()
        naverMap?.moveCamera(CameraUpdate.fitBounds(bounds, 200))
    }

    private fun moveMyLocation() {
        naverMap?.moveCamera(CameraUpdate.scrollTo(naverLatLng.last()))
    }

    private fun stopService(){
        sendCommandToService(RUNNING_STATE.STOP.name)
    }


    private fun drawPolyline() {
        polyline.coords = naverLatLng
        polyline.color = getColor(R.color.mainColor)
        polyline.map = naverMap
    }

    private fun changeTimeText(time: String) {
        binding.apply {
            tvTime.text = time

            if (runningViewModel.goalType.value == GOAL_TYPE.TIME) {
                /** goal amount 변경 **/
            }
        }
    }

    private fun changeDistanceText(sumDistance: Float) {
        binding.apply {
            tvDistance.text = TrackingUtility.getFormattedDistance(sumDistance)

            if (runningViewModel.goalType.value == GOAL_TYPE.DISTANCE) {
                /** goal amount 변경 **/
            }
        }
    }

    private fun changeCalorie(sumDistance: Float) {
        caloriesBurned = ((sumDistance / 1000f) * runningViewModel.weight.value / 1.036).toInt()
        binding.tvCalorie.text = "$caloriesBurned"
    }


    // 서비스에게 명령을 전달함
    private fun sendCommandToService(action: String) {
        Intent(this, RunningService::class.java).also {
            it.action = action
            this.startService(it)
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val bind = service as RunningService.LocalBinder
            runningService = bind.getService()
            initObserve()
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        // 라이트 모드 설정 시 지도 심벌의 클릭 이벤트를 처리할 수 없습니다
        this.naverMap = naverMap

        /** apply 적용 시 내 위치로 이동하지 않는 현상 **/
        naverMap.moveCamera(CameraUpdate.zoomTo(16.0))
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
            // 현위치 버튼 활성화
        naverMap.uiSettings.isLocationButtonEnabled = true

        // 액티비티 재시작 시 naverMap이 ready되지 않은 상태에서 observer되어
        // 경로가 그려지지 않는 현상 발생
        val isOkToDrawPolyline = naverLatLng.size >= 2
        if (isOkToDrawPolyline) {
            drawPolyline()
        }
    }

    private fun loading(timeinMillis: Long) {
        loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        // 로딩이 진행되지 않았을 경우
        lifecycleScope.launch {
            delay(timeinMillis)
            if (loadingDialog.isShowing) {
                loadingDialog.dismiss()
            }
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val builder = AlertDialog.Builder(this@RunningActivity)
            builder.setTitle("달리기를 종료할까요? 10초 이하의 기록은 저장되지 않습니다.")
                .setPositiveButton("네"){ _,_ ->
                    stopRun()
                }
                .setNegativeButton("아니오"){_,_ ->
                    // 다시 시작
                }.create()
            builder.show()
        }
    }

    private fun List<Location>.mapperToListNaverLatLng(): List<LatLng> = this.map {
        LatLng(it.latitude, it.longitude)
    }

}