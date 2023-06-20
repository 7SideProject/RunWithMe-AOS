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
import com.side.domain.model.Coordinate
import com.side.domain.model.RunRecord
import com.side.runwithme.R
import com.side.runwithme.databinding.ActivityRunningBinding
import com.side.runwithme.service.PolyLine
import com.side.runwithme.service.RunningService
import com.side.runwithme.service.SERVICE_NOTSTART
import com.side.runwithme.util.*
import com.side.runwithme.view.MainActivity
import com.side.runwithme.view.loading.LoadingDialog
import com.side.runwithme.view.login.LoginViewModel
import com.side.runwithme.view.running_result.RunningResultActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Math.round
import java.time.LocalDate
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

    private var naverLatLng = mutableListOf<LatLng>()

    private lateinit var runningService: RunningService

    private var type: String = GOAL_TYPE_TIME
    private var goal = 60 * 1000L

    // 라이브 데이터 받아온 값들
    private var caloriesBurned: Int = 0
    private var sumDistance: Float = 0f
    private var currentTimeInMillis = 0L
    private var challengeSeq: Int = 0
    private lateinit var imgFile: MultipartBody.Part
    private val coordinates: MutableList<Coordinate> = mutableListOf()
    private lateinit var runRecord: RunRecord
    private var isStopError = false

    private lateinit var loadingDialog: LoadingDialog

    @RequiresApi(Build.VERSION_CODES.O)
    override fun init() {
        val intent = Intent()
        challengeSeq = intent.getIntExtra("challengeSeq", 0)

        /** goalType, goal 받아와야함 **/

        initMapView()

        initClickListener()

        if (RunningService.serviceState == SERVICE_NOTSTART) {
            runningViewModel.saveChallengeSeqInViewModel(challengeSeq)
            runningViewModel.getMyWeight()
            firstStart()
        } else { // app killed 된 후 activity 재시작
            bindService()
        }

        initDrawLine()

        initViewModelCallback()
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
                val intent = Intent(this, RunningResultActivity::class.java)
                intent.putExtra(
                    "allRunRecord",
                    AllRunRecord(
                        runRecord = runRecord,
                        coordinates = emptyList(),
                        imgFile = imgFile
                    )
                )
                startActivity(Intent(this, RunningResultActivity::class.java))
                finish()
            }
            is RunningViewModel.Event.Fail -> {
                showToast("다시 한 번 시도해주세요.")
            }
            is RunningViewModel.Event.ServerError -> {
                showToast("서버가 불안정합니다. 다시 한 번 시도해주세요.")
            }
        }
    }

    private fun initClickListener() {
        binding.apply {
            ibStart.setOnClickListener {
                runningBtnUI()

                sendCommandToService(ACTION_RESUME_SERVICE)
            }

            ibPause.setOnClickListener {
                pauseBtnUI()

                sendCommandToService(ACTION_PAUSE_SERVICE)
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

    private fun endToSaveData() {
        takeSnapShot()
        changeCoordinates()
        saveRunRecord()
    }

    private fun saveRunRecord() {
        runRecord = RunRecord(
            runRecordSeq = 0,
            runImageSeq = 0,
            runningDay = runningService.startDay,
            runningStartTime = timeFormatter(runningService.startTime),
            runningEndTime = timeFormatter(System.currentTimeMillis()),
            runningTime = (currentTimeInMillis / 1000).toInt(),
            runningDistance = sumDistance.toInt(),
            runningAvgSpeed = 1.0 * (round(sumDistance / 1000f) / (currentTimeInMillis / 1000f / 60 / 60) * 10) / 10f,
            runningCalorieBurned = caloriesBurned,
            runningStartingLat = coordinates.first().latitude,
            runningStartingLng = coordinates.first().longitude,
            completed = "",
            userName = "",
            userSeq = 0,
            challengeName = "",
            challengeSeq = 0
        )
    }

    private fun changeCoordinates() {
        for (latlng in naverLatLng) {
            coordinates.add(Coordinate(latlng.latitude, latlng.longitude))
        }
    }

    private fun takeSnapShot() {
        moveLatLngBounds()

        naverMap?.takeSnapshot {
            // image 생성
            imgFile = createMultiPart(it)

        }
    }

    private fun createMultiPart(bitmap: Bitmap): MultipartBody.Part {
        var imageFile: File? = null
        try {
            imageFile = createFileFromBitmap(bitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), imageFile!!)
        return MultipartBody.Part.createFormData("imgFile", imageFile!!.name, requestFile)
    }

    @Throws(IOException::class)
    private fun createFileFromBitmap(bitmap: Bitmap): File? {
        val newFile = File(this.filesDir, "profile_${System.currentTimeMillis()}")
        val fileOutputStream = FileOutputStream(newFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, fileOutputStream)
        fileOutputStream.close()
        return newFile
    }

    // 이동한 전체 polyLine 담기
    private fun moveLatLngBounds() {
        val latLngBoundsBuilder = LatLngBounds.Builder().include(naverLatLng)
        val bounds = latLngBoundsBuilder.build()
        naverMap?.moveCamera(CameraUpdate.fitBounds(bounds, 300))
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
                naverLatLng = it

                val isOkToDrawPolyline = it.size >= 2
                if (isOkToDrawPolyline && naverMap != null) {
                    drawPolyline()
                }
            }
        }

        // 시간(타이머) 경과 observe
        runningService.timeRunInMillis.observe(this) {
            currentTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTimeSummery(it)
            changeTimeText(formattedTime)

            // 프로그래스바 진행도 변경
            if (it > 0 && type == GOAL_TYPE_TIME) {
                binding.progressBarGoal.progress =
                    if ((it / (goal / 100)).toInt() >= 100) 100 else (it / (goal / 100)).toInt()
            }
        }

        // 거리 observe
        runningService.sumDistance.observe(this) {
            sumDistance = it
            changeDistanceText(sumDistance)
            changeCalorie(sumDistance)

            // 프로그래스바 진행도 변경
            if (sumDistance > 0 && type == GOAL_TYPE_DISTANCE) {
                binding.progressBarGoal.progress =
                    if ((sumDistance / (goal / 100)).toInt() >= 100) 100 else (sumDistance / (goal / 100)).toInt()
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
        sendCommandToService(ACTION_SHOW_RUNNING_ACTIVITY)
        delay(3000L)

        sendCommandToService(ACTION_START_SERVICE)

        bindService()

        delay(1000L)
    }

    // 달리기 종료
    private fun stopRun() {
        runningService.stopRunningBeforeRegister = true

        if (!isStopError) { // 서버 에러 등으로 다시 stop을 눌러야할 때 한 번 더 저장 안하도록, (bitmap 하나 더 생성하기 때문에 메모리 누수 우려)
            endToSaveData()
            isStopError = true
        }

        runningViewModel.postRunRecord(
            AllRunRecord(
                runRecord = runRecord,
                coordinates = coordinates,
                imgFile = imgFile
            )
        )

        lifecycleScope.launch {
            loading(5000L)
        }

    }

    private fun stopService(){
        unbindService(serviceConnection)
        sendCommandToService(ACTION_STOP_SERVICE)
    }


    private fun drawPolyline() {
        polyline.coords = naverLatLng
        polyline.color = getColor(R.color.mainColor)
        polyline.map = naverMap
    }

    private fun changeTimeText(time: String) {
        binding.apply {
            tvTime.text = time

            if (type == GOAL_TYPE_TIME) {
                /** goal amount 변경 **/
            }
        }
    }

    private fun changeDistanceText(sumDistance: Float) {
        binding.apply {
            tvDistance.text = TrackingUtility.getFormattedDistance(sumDistance)

            if (type == GOAL_TYPE_DISTANCE) {
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
        naverMap.isLiteModeEnabled = true
            // 현위치 버튼 활성화
        naverMap.uiSettings.isLocationButtonEnabled = true
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

    // 뒤로가기 버튼 눌렀을 때
    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
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