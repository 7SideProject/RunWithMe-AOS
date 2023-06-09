package com.side.runwithme.binding

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.side.domain.model.Challenge
import com.side.domain.utils.ResultType
import com.side.runwithme.view.challenge.ChallengeListAdapter

object RecyclerBinding {

    @BindingAdapter("submitList")
    @JvmStatic
    fun bindSubmitList(view : RecyclerView, result : ResultType<*>?){
        if(result is ResultType.Success){
            when(view.adapter){
                is ChallengeListAdapter -> {
                    Log.d("test123", "bindSubmitList: ")
                    (view.adapter as ListAdapter<Any,*>).submitList(result.data as List<Challenge>)
                }
            }
        }
    }
}