package com.side.runwithme.view.challenge.create

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
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ChallengeCreate1Fragment : BaseFragment<FragmentChallengeCreate1Binding>(R.layout.fragment_challenge_create1) {

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
                findNavController().navigate(ChallengeCreate1FragmentDirections.actionChallengeCreate1FragmentToChallengeCreate2Fragment())
            }

            toolbar.setBackButtonClickEvent {
                findNavController().popBackStack()
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

            var bitmap : Bitmap? = null
            val uri = it.data?.data
            try {
                if(uri != null){
                    bitmap = resizeBitmapFormUri(uri,requireContext())

                    if(bitmap == null) return@registerForActivityResult

                    challengeCreateViewModel.challengeImgMultiPart.value = createMultiPart(bitmap)
                }
            } catch (e : Exception){
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    private fun createFileFromBitmap(bitmap: Bitmap): File {
        val newFile = File(requireActivity().filesDir, "challenge_${System.currentTimeMillis()}")
        val fileOutputStream = FileOutputStream(newFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, fileOutputStream)
        fileOutputStream.close()
        return newFile
    }



    private fun createMultiPart(bitmap: Bitmap): MultipartBody.Part? {
        var imageFile: File? = null
        try {
            imageFile = createFileFromBitmap(bitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val mediaType = "multipart/form-data".toMediaTypeOrNull()
        val requestFile = imageFile?.asRequestBody(mediaType) ?: return null

        return MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
    }


}