package com.side.runwithme.view.challenge_create

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentChallengeCreate1Binding
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.util.resizeBitmapFormUri
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class ChallengeCreateStep1Fragment : BaseFragment<FragmentChallengeCreate1Binding>(R.layout.fragment_challenge_create1) {

    private val challengeCreateViewModel by activityViewModels<ChallengeCreateViewModel>()

    override fun init() {

        binding.apply {
            challengeCreateVM = challengeCreateViewModel
        }

        initClickListener()

        initViewModelCallback()

    }

    private fun initClickListener(){
        binding.apply {
            layoutChallengeImg.setOnClickListener {
                pickPhotoGallery()
            }

            btnNext.setOnClickListener {
                findNavController().navigate(ChallengeCreateStep1FragmentDirections.actionChallengeCreateStep1FragmentToChallengeCreateStep2Fragment())
            }

            toolbar.setBackButtonClickEvent {
                requireActivity().finish()
            }
        }
    }

    private fun initViewModelCallback(){
        challengeCreateViewModel.apply {

            repeatOnStarted {
                challengeImg.collectLatest {
                    applyChallengeImg(it)
                }
            }

        }
    }

    private fun applyChallengeImg(uri: Uri?){
        if(uri == null) return

        binding.ivChallenge.setImageURI(uri)
        binding.ivChallenge.alpha = 1.0F
        binding.apply {
            tvChallengeImageNotice.visibility = View.GONE
            tvChallengeImageSelect.visibility = View.GONE
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
            challengeCreateViewModel.challengeImg.value = it.data?.data

            val uri = it.data?.data
            try {
                if(uri != null){
                    val bitmap = resizeBitmapFormUri(uri,requireContext())

                    if(bitmap == null) return@registerForActivityResult

                    challengeCreateViewModel.challengeImgMultiPart.value = createMultiPart(createByteArray(bitmap))
                }
            } catch (e : Exception){
                e.printStackTrace()
            }
        }
    }

    private fun createByteArray(bitmap: Bitmap): ByteArray{
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, outputStream)
        return outputStream.toByteArray()
    }

    private fun createMultiPart(imageByteArray: ByteArray): MultipartBody.Part {
        val requestFile = imageByteArray.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", "challenge", requestFile)
    }

}