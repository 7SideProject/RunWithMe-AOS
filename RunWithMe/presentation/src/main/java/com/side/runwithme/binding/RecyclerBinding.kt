package com.side.runwithme.binding

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.side.domain.model.Challenge
import com.side.domain.utils.ResultType
import com.side.runwithme.view.challenge.ChallengeListAdapter
import com.side.runwithme.view.running_list.RunningListAdapter
import kotlinx.coroutines.flow.MutableStateFlow


@BindingAdapter("submitList")
fun bindSubmitList(view: RecyclerView, result: ResultType<*>?) {
    if (result is ResultType.Success) {
        when (view.adapter) {
            is ChallengeListAdapter -> {
                (view.adapter as ListAdapter<Any, *>).submitList(result.data as List<Challenge>)
            }
        }
    }
}

@BindingAdapter("submitList2")
fun bindSubmitList(view: RecyclerView, result: List<*>?) {
    when (view.adapter) {
        is RunningListAdapter -> {
            (view.adapter as ListAdapter<Any, *>).submitList(result!! as List<Challenge>)
        }
    }

}
