package com.side.runwithme.view.challenge_board

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.target.Target
import com.side.runwithme.R
import com.side.runwithme.util.BASE_URL
import com.side.runwithme.util.CHALLENGE
import com.side.runwithme.util.GET_BOARD_IMG
import de.hdodenhof.circleimageview.CircleImageView


@BindingAdapter("boardContentImage", "boardSeq", "jwt")
fun ImageView.setBoardContentImage(boardContentImage: Boolean, boardSeq: Long, jwt: String){
    if(boardContentImage){
        this.visibility = View.VISIBLE
        Log.d("test123", "setBoardContentImage: ${jwt}")
        val glideUrl = GlideUrl(BASE_URL + CHALLENGE + "/${boardSeq}/" + GET_BOARD_IMG){
            mapOf(Pair("Authorization", jwt))
        }
        Glide.with(this.context).load(glideUrl).override(Target.SIZE_ORIGINAL, 500).fitCenter().placeholder(R.drawable.bg_lightgrey).diskCacheStrategy(
            DiskCacheStrategy.RESOURCE).into(this)
    } else {
        this.visibility = View.GONE
    }
}