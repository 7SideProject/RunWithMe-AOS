package com.side.runwithme.view.running_list

import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.side.domain.model.Challenge
import com.side.runwithme.databinding.ItemMyCurrentChallengeListBinding
import com.side.runwithme.model.Token
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


class RunningListAdapter(private val listener: IntentToRunningActivityClickListener, private val jwt: StateFlow<String>) : PagingDataAdapter<Challenge, RunningListAdapter.ViewHolder>(diffUtil) {

    class ViewHolder(private val binding: ItemMyCurrentChallengeListBinding, private val jwt: String): RecyclerView.ViewHolder(binding.root){

        fun bind(challenge: Challenge, listener: IntentToRunningActivityClickListener){
            binding.challenge = challenge
            binding.token = Token(jwt)
            binding.root.setOnClickListener {
                listener.onItemClick(challenge)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyCurrentChallengeListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding, jwt.value)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if(item != null) {
            holder.bind(item, listener)
        }
    }

    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<Challenge>(){
            override fun areItemsTheSame(oldItem: Challenge, newItem: Challenge): Boolean {
                return oldItem.seq == newItem.seq
            }

            override fun areContentsTheSame(oldItem: Challenge, newItem: Challenge): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }
    }
}