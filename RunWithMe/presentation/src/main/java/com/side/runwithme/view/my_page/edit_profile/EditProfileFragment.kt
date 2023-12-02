package com.side.runwithme.view.my_page.edit_profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentEditProfileBinding
import com.side.runwithme.util.BASE_URL
import com.side.runwithme.util.GET_PROFILE_IMG
import com.side.runwithme.util.USER
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.util.resizeBitmapFormUri
import com.side.runwithme.view.my_page.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>(R.layout.fragment_edit_profile) {

    private val myPageViewModel by activityViewModels<MyPageViewModel>()

    override fun init() {
        binding.apply {
            myPageVm = myPageViewModel
        }

        initSpinner()
        initClickListener()
        initViewModelCallbacks()
    }

    private fun initSpinner(){
        initWeightSpinner()
        initHeightSpinner()
    }

    private fun initClickListener(){
        binding.apply {
            btnModify.setOnClickListener {
                setMultiPartBody()
                myPageViewModel.editProfileRequest()
            }
            ivUserProfile.setOnClickListener {
                pickPhotoGallery()
            }
            ivEditPhoto.setOnClickListener {
                pickPhotoGallery()
            }
        }
    }

    private fun setMultiPartBody(){
        try {
            if(myPageViewModel.profileImgUri.value != null){
                val bitmap = resizeBitmapFormUri(myPageViewModel.profileImgUri.value!!,requireContext())
                if(bitmap == null) return
                myPageViewModel.setProfileImg(createMultiPart(createByteArray(bitmap)))
            }
        } catch (e : Exception){
            e.printStackTrace()
        }
    }

    private fun initViewModelCallbacks(){
        repeatOnStarted {
            myPageViewModel.editEventFlow.collectLatest {
                when(it){
                    is MyPageViewModel.EditEvent.Success -> {
                        showToast(resources.getString(it.message))
                        findNavController().popBackStack()
                    }
                    is MyPageViewModel.EditEvent.Fail-> {
                        showToast(resources.getString(it.message))
                    }
                    is MyPageViewModel.EditEvent.Error -> {
                        showToast("서버 에러입니다. 다시 시도해주세요.")
                    }
                }
            }
        }

        lifecycleScope.launch {
            myPageViewModel.profileImgUri.collectLatest {
                if(it != null) {
                    binding.ivUserProfile.setImageURI(it)
                }
            }
        }

    }


    private fun initWeightSpinner(){
        val weightList = Array(231) { i -> i + 20 }

        binding.spinnerEditWeight.apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, weightList)
            setSelection(myPageViewModel.userProfile.value.weight - 20)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
                AdapterView.OnItemClickListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    myPageViewModel.setWeight(weightList[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    myPageViewModel.setWeight(weightList[position])
                }
            }
        }
    }
    private fun initHeightSpinner(){
        val heightList = Array(131) { i -> i + 120 }

        binding.spinnerEditHeight.apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, heightList)
            setSelection(myPageViewModel.userProfile.value.height - 120)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
                AdapterView.OnItemClickListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    myPageViewModel.setHeight(heightList[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    myPageViewModel.setHeight(heightList[position])
                }
            }
        }
    }

    private fun pickPhotoGallery() {
        val photoIntent = Intent(Intent.ACTION_PICK)
        photoIntent.type = "image/*"
        photoIntent.putExtra("crop","true")
        pickPhotoResult.launch(photoIntent)
    }

    private val pickPhotoResult : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){

        if(it.resultCode == Activity.RESULT_OK){
            val uri = it.data?.data
            myPageViewModel.setProfileImgUri(uri)

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

    private fun createMultiPart(imageByteArray: ByteArray): MultipartBody.Part {
        val requestFile = imageByteArray.toRequestBody("image/webp".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", "profile", requestFile)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        myPageViewModel.setProfileImg(null)
        myPageViewModel.setProfileImgUri(null)
    }
}