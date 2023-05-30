package com.side.runwithme.view.challenge

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.side.domain.model.Challenge
import com.side.runwithme.databinding.ItemChallengeListBinding

class ChallengeListAdapter() : ListAdapter<Challenge, ChallengeListAdapter.ChallengeListViewHolder>(diffUtil) {

    inner class ChallengeListViewHolder(val binding: ItemChallengeListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(challenge: Challenge) {
            binding.apply {
                this.challenge = challenge
//                tvChallengeMember.text = "${challenge.memberCount} / ${challenge.totalMemberCount}"
//                tvChallengeCost.text = "${challenge.cost} P"
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
            holder.bind(item)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Challenge>() {
            override fun areContentsTheSame(oldItem: Challenge, newItem: Challenge) =
                oldItem.hashCode() == newItem.hashCode()

            override fun areItemsTheSame(oldItem: Challenge, newItem: Challenge) =
                oldItem.seq == newItem.seq
        }
    }

}