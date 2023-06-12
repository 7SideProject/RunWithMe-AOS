package com.side.runwithme.view.running_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.side.domain.model.Challenge
import com.side.runwithme.databinding.ItemMyCurrentChallengeListBinding

class RunningListAdapter(private val listener: IntentToRunningActivityClickListener) : ListAdapter<Challenge, RunningListAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemMyCurrentChallengeListBinding): RecyclerView.ViewHolder(binding.root){

        init {
            binding.root.setOnClickListener {
                listener.onItemClick(getItem(absoluteAdapterPosition).seq)
            }
        }

        fun bind(challenge: Challenge){
            binding.challenge = challenge
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyCurrentChallengeListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
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