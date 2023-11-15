package com.side.runwithme.view.permission

import android.content.Intent
import android.net.Uri
import com.example.seobaseview.base.BaseActivity
import com.side.runwithme.R
import com.side.runwithme.databinding.ActivityPermissionBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PermissionActivity : BaseActivity<ActivityPermissionBinding>(R.layout.activity_permission) {

    override fun init() {
        initClickListener()
    }

    private fun initClickListener(){
        binding.apply {
            btnOk.setOnClickListener {
                finish()
            }
            tvTerms.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://paper-fir-422.notion.site/970bb6b90ac945fa99b6368262dd0c3c?pvs=4")
                startActivity(i)
            }
        }
    }


}