package com.side.runwithme.view.challenge_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.side.domain.model.Challenge
import com.side.runwithme.databinding.ItemChallengeListBinding
import com.side.runwithme.view.my_challenge.MyChallengeListAdapterClickListener

class ChallengeListAdapter(private val challengeListAdapterClickListener: ChallengeListAdapterClickListener) : PagingDataAdapter<Challenge, ChallengeListAdapter.ChallengeListViewHolder>(diffUtil) {

    class ChallengeListViewHolder(val binding: ItemChallengeListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(challenge: Challenge, challengeListAdapterClickListener: ChallengeListAdapterClickListener) {
            binding.apply {
                this.challenge = challenge

                root.setOnClickListener {
                    challengeListAdapterClickListener.onClick(challenge)
                }
                executePendingBindings()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeListViewHolder {
        val binding =
            ItemChallengeListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChallengeListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChallengeListViewHolder, position: Int) {
        val item = getItem(position)

        if (item != null) {
            holder.bind(item, challengeListAdapterClickListener)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Challenge>() {
            override fun areContentsTheSame(oldItem: Challenge, newItem: Challenge) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Challenge, newItem: Challenge) =
                oldItem.seq == newItem.seq
        }
    }

}