package com.side.runwithme.view.running_result

import androidx.lifecycle.ViewModel
import com.side.domain.model.RunRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class RunningResultViewModel @Inject constructor(

): ViewModel(){

    private var _runRecord: RunRecord? = null
    val runRecord get() = _runRecord

    private var _imgFile: MultipartBody.Part? = null
    val imgFile get() = _imgFile

    fun setRunRecord(runRecord: RunRecord?) {
        this._runRecord = runRecord
    }

    fun setImgFile(imgFile: MultipartBody.Part?){
        this._imgFile = imgFile
    }

}