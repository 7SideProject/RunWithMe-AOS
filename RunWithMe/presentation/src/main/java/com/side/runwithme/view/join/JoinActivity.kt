package com.side.runwithme.view.join

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.text.InputFilter
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.seobaseview.base.BaseActivity
import com.side.domain.model.User
import com.side.runwithme.R
import com.side.runwithme.databinding.ActivityJoinBinding
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.util.resizeBitmapFormUri
import kotlinx.coroutines.flow.collectLatest
import com.side.runwithme.view.join.JoinViewModel.Event
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.regex.Pattern

@AndroidEntryPoint
class JoinActivity : BaseActivity<ActivityJoinBinding>(R.layout.activity_join) {

    private val joinViewModel by viewModels<JoinViewModel>()
    override fun init() {
        initClickListener()
        initSpinner()
        initViewModelCallBack()
    }

    private fun initClickListener() {
        binding.apply {
            btnJoin.setOnClickListener {
                joinViewModel.join()
            }
            civUserProfile.setOnClickListener {
                pickPhotoGallery()
            }
            ivEditPhoto.setOnClickListener {
                pickPhotoGallery()
            }
            toolbar.setBackButtonClickEvent {
                finish()
            }
        }
    }

    private val pickPhotoResult : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK){
            binding.civUserProfile.setImageURI(it.data?.data)

            var bitmap : Bitmap?
            val uri = it.data?.data
            try {
                if(uri != null){
                    bitmap = resizeBitmapFormUri(uri, this)
                    createMultiPart(bitmap!!)
                }
            } catch (e : Exception){
                e.printStackTrace()
            }
        }
    }

    private fun createMultiPart(bitmap: Bitmap) {
        var imageFile: File? = null
        try {
            imageFile = createFileFromBitmap(bitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), imageFile!!)
        joinViewModel.setImgFile(MultipartBody.Part.createFormData("imgFile", imageFile!!.name, requestFile))
    }

    @Throws(IOException::class)
    private fun createFileFromBitmap(bitmap: Bitmap): File? {
        val newFile = File(this.filesDir, "profile_${System.currentTimeMillis()}")
        val fileOutputStream = FileOutputStream(newFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, fileOutputStream)
        fileOutputStream.close()
        return newFile
    }

    private fun initNickNameRule(){
        binding.etJoinNickname.filters = arrayOf(
            InputFilter { src, start, end, dst, dstart, dend ->
                // val ps = Pattern.compile("^[a-zA-Z0-9ㄱ-ㅎ가-흐]+$") // 영문 숫자 한글
                // 영문 숫자 한글 천지인 middle dot[ᆞ]
                val ps = Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$")
                if (src.equals("") || ps.matcher(src).matches()) {
                    return@InputFilter src;
                }
                showToast("닉네임은 한글, 영문, 숫자로만 2자 ~ 8자까지 입력 가능합니다.")
                return@InputFilter "";
            },
            InputFilter.LengthFilter(8)
        )
    }

    private fun pickPhotoGallery() {
        val photoIntent = Intent(Intent.ACTION_PICK)
        photoIntent.type = "image/*"
        photoIntent.putExtra("crop","true")
        pickPhotoResult.launch(photoIntent)
    }
    private fun initSpinner(){
        initWeightSpinner()
        initHeightSpinner()
    }

    private fun initWeightSpinner(){
        val weightList = Array(231) { i -> i + 20 }

        binding.spinnerEditWeight.apply {
            adapter = ArrayAdapter(this@JoinActivity, android.R.layout.simple_spinner_dropdown_item, weightList)
            setSelection(30)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
                AdapterView.OnItemClickListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    joinViewModel.setWeight(weightList.get(position))
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    joinViewModel.setWeight(weightList.get(position))
                }
            }
        }
    }
    private fun initHeightSpinner(){
        val heightList = Array(131) { i -> i + 120 }

        binding.spinnerEditHeight.apply {
            adapter = ArrayAdapter(this@JoinActivity, android.R.layout.simple_spinner_dropdown_item, heightList)
            setSelection(30)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
                AdapterView.OnItemClickListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    joinViewModel.setHeight(heightList.get(position))
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    joinViewModel.setHeight(heightList.get(position))
                }
            }
        }
    }

    private fun initViewModelCallBack() {

        repeatOnStarted {
            joinViewModel.joinEventFlow.collectLatest { event ->
                handleEvent(event)

            }
        }

    }

    private fun handleEvent(event: Event) {
        when (event) {
            is Event.Success -> {
                showToast(event.message)
                finish()
            }
            is Event.Fail -> {
                showToast(event.message)
            }
        }
    }

}