package com.side.runwithme.binding

import androidx.databinding.BindingAdapter
import com.willy.ratingbar.ScaleRatingBar

object ViewBindingAdapter {

    @BindingAdapter("hardPoint")
    @JvmStatic
    fun ScaleRatingBar.setHardPoint(point: Int){
        this.rating = point.toFloat()
    }

}