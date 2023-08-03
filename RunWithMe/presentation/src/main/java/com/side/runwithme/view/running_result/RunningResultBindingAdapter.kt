package com.side.runwithme.view.running_result

import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.side.runwithme.R

@BindingAdapter("runRecordImgByByteArray")
fun ImageView.setRunRecordImgFileByByteArray(imgFile: ByteArray?) {

    if(imgFile == null) {
        Glide.with(this.context).load(R.drawable.running_record).into(this)
        return
    }


    val bitmap = BitmapFactory.decodeByteArray(imgFile, 0, imgFile.size)
    Glide.with(this.context)
        .load(bitmap)
        .into(this)
}
