package com.side.runwithme.view.create_board

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.side.runwithme.R
import com.side.runwithme.base.BaseFragment
import com.side.runwithme.databinding.FragmentCreateBoardBinding
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.util.resizeBitmapFormUri
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class CreateBoardFragment: BaseFragment<FragmentCreateBoardBinding>(R.layout.fragment_create_board) {

    private val args by navArgs<CreateBoardFragmentArgs>()
    private val createBoardViewModel : CreateBoardViewModel by viewModels()
    private var img: MultipartBody.Part? = null

    override fun init() {

        val challengeSeq = args.challengeSeq

        if(challengeSeq == 0L){
            showToast("잘못된 접근입니다.")
            findNavController().popBackStack()
        }

        binding.apply {
            createBoardVM = createBoardViewModel
        }

        createBoardViewModel.setChallengeSeq(challengeSeq)

        initClickListener()

        initViewModelCallbacks()
    }

    private fun initClickListener(){
        binding.apply {
            btnCancel.setOnClickListener {
                findNavController().popBackStack()
            }
            btnRecommend.setOnClickListener {
                createBoardViewModel.createBoard(img)
            }

            ivRecommend.setOnClickListener {
                pickPhotoGallery()
            }
            ivRecommendPhoto.setOnClickListener {
                pickPhotoGallery()
            }
        }
    }

    private fun initViewModelCallbacks(){
        createBoardViewModel.apply {
            repeatOnStarted {
                createEventFlow.collectLatest {
                    when(it){
                        is CreateBoardViewModel.Event.Success -> {
                            showToast("게시글 등록 완료")
                            findNavController().popBackStack()
                        }
                        is CreateBoardViewModel.Event.Fail -> {
                            showToast(it.message)
                        }
                    }
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
            try {
                if(uri != null){

                    binding.apply {
                        ivRecommendPhoto.visibility = View.GONE
                        ivRecommend.setImageURI(uri)
                    }

                    val bitmap = resizeBitmapFormUri(uri,requireContext())
                    if(bitmap == null) return@registerForActivityResult

                    img = createMultiPart(createByteArray(bitmap))
                }
            } catch (e : Exception){
                e.printStackTrace()
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

    private fun createMultiPart(imageByteArray: ByteArray): MultipartBody.Part {
        imageByteArray.toRequestBody()
        val requestFile = imageByteArray.toRequestBody("image/webp".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", "challenge", requestFile)
    }
}