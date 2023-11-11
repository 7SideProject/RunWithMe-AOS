package com.side.runwithme.view.running_result

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.seobaseview.base.BaseActivity
import com.side.domain.model.AllRunRecord
import com.side.domain.model.Coordinate
import com.side.domain.model.RunRecord
import com.side.runwithme.R
import com.side.runwithme.databinding.ActivityRunningResultBinding
import com.side.runwithme.mapper.mapperToCoordinate
import com.side.runwithme.mapper.mapperToRunRecord
import com.side.runwithme.model.Coordinates
import com.side.runwithme.model.RunRecordParcelable
import com.side.runwithme.view.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunningResultActivity : BaseActivity<ActivityRunningResultBinding>(R.layout.activity_running_result) {

    private lateinit var navController : NavController

    private val runningResultViewModel : RunningResultViewModel by viewModels<RunningResultViewModel>()


    override fun init() {
        initNavigation()

        initIntentExtra()

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun initIntentExtra(){
        val intent = intent
        val runRecord : RunRecordParcelable?
        val coordinates : List<Coordinates>?
        val imgByteArray : ByteArray? = intent.getByteArrayExtra("imgByteArray")
        val challengeName = intent.getStringExtra("challengeName")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            runRecord = intent.getParcelableExtra("runRecord", RunRecordParcelable::class.java)
            coordinates = intent.getParcelableArrayListExtra("coordinates", Coordinates::class.java)
        }else {
            runRecord = (intent.getParcelableExtra("runRecord") as RunRecordParcelable?)
            coordinates = (intent.getParcelableArrayListExtra<Coordinates>("coordinates"))
        }



        runningResultViewModel.apply {
            if(runRecord != null) {
                putRunRecord(runRecord)
            }
            if(!coordinates.isNullOrEmpty()) {
                putCoordinates(coordinates.toTypedArray())
            }

            if(imgByteArray != null){
                putImgByteArray(imgByteArray)
            }

            if(challengeName != null){
                putChallengeName(challengeName)
            }
        }

    }

    private fun initNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view_result) as NavHostFragment
        navController = navHostFragment.navController
    }


    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if(navController.currentDestination?.id == R.id.runningResultFragment) {
                startActivity(Intent(this@RunningResultActivity, MainActivity::class.java))
                finish()
            }else {
                navController.popBackStack()
            }
        }
    }

}