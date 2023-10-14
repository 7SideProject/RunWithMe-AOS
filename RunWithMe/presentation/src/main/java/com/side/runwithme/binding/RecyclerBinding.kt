package com.side.runwithme.binding

import androidx.databinding.BindingAdapter
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.side.domain.model.Challenge
import com.side.domain.utils.ResultType
import com.side.runwithme.view.challenge_list.ChallengeListAdapter
import com.side.runwithme.view.running_list.RunningListAdapter


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
