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
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import com.example.seobaseview.base.BaseActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.side.domain.model.AllRunRecord
import com.side.domain.model.RunRecord
import com.side.runwithme.R
import com.side.runwithme.databinding.ActivityRunningBinding
import com.side.runwithme.mapper.mapperToCoordinate
import com.side.runwithme.mapper.mapperToCoordinatesList
import com.side.runwithme.mapper.mapperToRunRecordParcelable
import com.side.runwithme.model.Coordinates
import com.side.runwithme.service.RunningService
import com.side.runwithme.service.SERVICE_NOTSTART
import com.side.runwithme.util.*
import com.side.runwithme.view.MainActivity
import com.side.runwithme.view.loading.LoadingDialog
import com.side.runwithme.view.running_result.RunningResultActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Math.round
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

    private lateinit var imgFile: MultipartBody.Part
    private lateinit var imgByteArray: ByteArray
    private var coordinates: ArrayList<Coordinates> = arrayListOf()
    private lateinit var runRecord: RunRecord
    private var isStopError = false

    private lateinit var loadingDialog: LoadingDialog


    @RequiresApi(Build.VERSION_CODES.O)
    override fun init() {
        val intent = intent
        val challengeSeq = intent.getIntExtra("challengeSeq", 0)
        val type = intent.getIntExtra("goalType", -1)
        val goal = intent.getLongExtra("goalAmount", 0)
        val challengeName = intent.getStringExtra("challengeName") ?: ""

        initMapView()

        initClickListener()

        // onBackPressed deprecated되고 아래처럼 사용해야함
        this.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        if (RunningService.serviceState == SERVICE_NOTSTART) {

            if(challengeSeq == 0 || type == -1 || goal == 0L || challengeName.isEmpty()){
                startError(resources.getString(R.string.running_error_not_found))
            }

            runningViewModel.saveChallengeInfo(challengeSeq, type, goal, challengeName)

            // 연습 모드는 -1, 나머지 challenge는 1이상이 들어와야함
            require(runningViewModel.challengeSeq.value != 0)

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
                loadingDialog.dismiss()
                // 서버에 등록이 완료된 후 service를 종료 시킴
                // 서버에 등록하기 전에 acitivity가 파괴되면 기록을 잃을 우려
                stopService()

                /** refresh 해줄 필요가 있는가? **/
                runningViewModel.saveChallengeInfo(0, -1, 0L, "")

                val intent = Intent(this, RunningResultActivity::class.java).apply {
                    putExtra("runRecord", runRecord.mapperToRunRecordParcelable())
                    putParcelableArrayListExtra("coordinates", coordinates)
//                    putExtra("challengeSeq", runningViewModel.challengeSeq.value)
                    putExtra("challengeName", runningViewModel.challengeName.value)
                    putExtra("imgByteArray", imgByteArray)
                }

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

                sendCommandToService(SERVICE_ACTION.RESUME.name)
            }

            ibPause.setOnClickListener {
                pauseBtnUI()

                sendCommandToService(SERVICE_ACTION.PAUSE.name)
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
        sendCommandToService(SERVICE_ACTION.FIRST_SHOW.name)
        delay(3000L)

        sendCommandToService(SERVICE_ACTION.START.name)

        bindService()

        delay(1000L)
    }

    // 달리기 종료
    private fun stopRun() {
        lifecycleScope.launch{
            if (!isStopError) { // 서버 에러 등으로 다시 stop을 눌러야할 때 한 번 더 저장 안하도록, (bitmap 하나 더 생성하기 때문에 메모리 누수 우려)
                endToSaveData()
                isStopError = true
            }

            runningService.stopRunningBeforeRegister = true

            if(runningViewModel.challengeSeq.value == -1){ // 연습러닝
                runningViewModel.postPracticeRunRecord(runRecord, imgByteArray)
            }else {
                runningViewModel.postChallengeRunRecord(
                    AllRunRecord(
                        runRecord = runRecord,
                        coordinates = coordinates.mapperToCoordinate(),
                        imgFile = imgFile
                    )
                )
            }

            loading(2000L)
        }

    }

    private suspend fun endToSaveData() {
        takeSnapShot()
        changeCoordinates()
        saveRunRecord()
        delay(500L)
    }

    private fun saveRunRecord() {
        var completed = "N"
        /** goalAmount하고 sumDistance, runningTime의 단위 맞춰야함 **/
        if(runningViewModel.goalType.value == GOAL_TYPE.DISTANCE && runningViewModel.goalAmount.value <= sumDistance){
            completed = "Y"
        }

        val runningTime = (currentTimeInMillis / 1000).toInt()

        if(runningViewModel.goalType.value == GOAL_TYPE.TIME && runningViewModel.goalAmount.value <= runningTime){
            completed = "Y"
        }


        runRecord = RunRecord(
            runRecordSeq = 0,
            runImageSeq = 0,
            runningDay = runningService.startDay,
            runningStartTime = timeFormatter(runningService.startTime),
            runningEndTime = timeFormatter(System.currentTimeMillis()),
            runningTime = runningTime,
            runningDistance = sumDistance.toInt(),
            runningAvgSpeed = 1.0 * (round(sumDistance / 1000f) / (currentTimeInMillis / 1000f / 60 / 60) * 10) / 10f,
            runningCalorieBurned = caloriesBurned,
            runningStartingLat = coordinates.first().latitude,
            runningStartingLng = coordinates.first().longitude,
            completed = completed,
            userName = "",
            userSeq = 0,
            challengeName = runningViewModel.challengeName.value,
            challengeSeq = runningViewModel.challengeSeq.value
        )
    }

    private fun changeCoordinates() {
        coordinates = naverLatLng.mapperToCoordinatesList()
    }

    private fun takeSnapShot() {
        moveLatLngBounds()

        naverMap?.takeSnapshot {
            // image 생성
            imgByteArray = createByteArray(it)
            imgFile = createMultiPart(imgByteArray)
        }
    }

    private fun createByteArray(bitmap: Bitmap): ByteArray{
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, outputStream)
        return outputStream.toByteArray()
    }

    private fun createMultiPart(imageByteArray: ByteArray): MultipartBody.Part {
        val requestFile = imageByteArray.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("imgFile", "running", requestFile)
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
        unbindService(serviceConnection)
        sendCommandToService(SERVICE_ACTION.STOP.name)
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
        caloriesBurned = round((sumDistance / 1000f) * runningViewModel.weight.value).toInt()
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

//    // 뒤로가기 버튼 눌렀을 때
//    @Deprecated("Deprecated in Java")
//    override fun onBackPressed() {
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("달리기를 종료할까요? 10초 이하의 기록은 저장되지 않습니다.")
//            .setPositiveButton("네"){ _,_ ->
//                stopRun()
//            }
//            .setNegativeButton("아니오"){_,_ ->
//                // 다시 시작
//            }.create()
//        builder.show()
//    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val builder = AlertDialog.Builder(this@RunningActivity)
            builder.setTitle("달리기를 종료할까요? 10초 이하의 기록은 저장되지 않습니다.")
                .setPositiveButton("네"){ _,_ ->
                    stopRun()
                    /** 한번 더 다이얼로그 띄워서 물어야함 or 꾹 누르기 **/
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