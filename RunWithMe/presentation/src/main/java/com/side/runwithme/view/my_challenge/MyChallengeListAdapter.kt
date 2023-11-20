package com.side.runwithme.view.my_challenge

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.side.domain.model.Challenge
import com.side.runwithme.databinding.ItemMyChallengeBinding
import com.side.runwithme.model.Token
import kotlinx.coroutines.flow.StateFlow

class MyChallengeListAdapter(private val myChallengeListAdapterClickListener: MyChallengeListAdapterClickListener, private val jwt: StateFlow<String>) : PagingDataAdapter<Challenge, MyChallengeListAdapter.ViewHolder>(diffUtil) {

    class ViewHolder(private val binding: ItemMyChallengeBinding, private val jwt: String): RecyclerView.ViewHolder(binding.root){

        fun bind(challenge: Challenge, myChallengeListAdapterClickListener: MyChallengeListAdapterClickListener){
            binding.apply {
                this.challenge = challenge
                token = Token(jwt)

                root.setOnClickListener {
                    myChallengeListAdapterClickListener.onClick(challenge)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyChallengeBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ViewHolder(binding, jwt.value)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if(item != null) {
            holder.bind(item, myChallengeListAdapterClickListener)
        }
    }

    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<Challenge>(){
            override fun areItemsTheSame(oldItem: Challenge, newItem: Challenge): Boolean {
                return oldItem.seq == newItem.seq
            }

            override fun areContentsTheSame(oldItem: Challenge, newItem: Challenge): Boolean {
                return oldItem == newItem
            }
        }
    }
}