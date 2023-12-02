package com.side.runwithme.view.running_result

import android.content.Intent
import android.os.Build
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.seobaseview.base.BaseActivity
import com.side.runwithme.R
import com.side.runwithme.databinding.ActivityRunningResultBinding
import com.side.runwithme.model.CoordinatesParcelable
import com.side.runwithme.model.RunRecordParcelable
import com.side.runwithme.view.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunningResultActivity :
    BaseActivity<ActivityRunningResultBinding>(R.layout.activity_running_result) {

    private lateinit var navController: NavController

    private val runningResultViewModel: RunningResultViewModel by viewModels<RunningResultViewModel>()


    override fun init() {
        initNavigation()

        initIntentExtra()

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun initIntentExtra() {
        val intent = intent
        val runRecord: RunRecordParcelable?
        val coordinates: List<CoordinatesParcelable>?
        val imgByteArray: ByteArray? = intent.getByteArrayExtra("imgByteArray")
        val challengeName = intent.getStringExtra("challengeName")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            runRecord = intent.getParcelableExtra("runRecord", RunRecordParcelable::class.java)
            coordinates =
                intent.getParcelableArrayListExtra("coordinates", CoordinatesParcelable::class.java)
        } else {
            runRecord = (intent.getParcelableExtra("runRecord") as RunRecordParcelable?)
            coordinates = (intent.getParcelableArrayListExtra<CoordinatesParcelable>("coordinates"))
        }

        runningResultViewModel.apply {
            if (runRecord != null) {
                putRunRecord(runRecord)
            }
            if (!coordinates.isNullOrEmpty()) {
                putCoordinates(coordinates.toTypedArray())
            }

            if (imgByteArray != null) {
                putImgByteArray(imgByteArray)
            }

            if (challengeName != null) {
                putChallengeName(challengeName)
            }
        }

    }

    private fun initNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view_result) as NavHostFragment
        navController = navHostFragment.navController
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (navController.currentDestination?.id == R.id.runningResultFragment) {
                startActivity(Intent(this@RunningResultActivity, MainActivity::class.java))
                finish()
            } else {
                navController.popBackStack()
            }
        }
    }

}