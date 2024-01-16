package com.side.runwithme.view.challenge_records

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.side.domain.model.ChallengeRunRecord
import com.side.domain.model.RunRecord
import com.side.runwithme.databinding.ItemRunningRecordBinding

class ChallengeRecordsAdapter(private val challengeRecordsClickListener: ChallengeRecordsClickListener) : PagingDataAdapter<ChallengeRunRecord, ChallengeRecordsAdapter.ChallengeRunRecordViewHolder>(diffUtil){
    class ChallengeRunRecordViewHolder(val binding: ItemRunningRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(record: ChallengeRunRecord, challengeRecordsClickListener: ChallengeRecordsClickListener) {
            binding.apply {
                this.record = record

                root.setOnClickListener {
                    challengeRecordsClickListener.onClick(record)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeRunRecordViewHolder {
        val binding =
            ItemRunningRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChallengeRunRecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChallengeRunRecordViewHolder, position: Int) {
        val item = getItem(position)

        if (item != null) {
            holder.bind(item, challengeRecordsClickListener)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChallengeRunRecord>() {
            override fun areContentsTheSame(oldItem: ChallengeRunRecord, newItem: ChallengeRunRecord) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: ChallengeRunRecord, newItem: ChallengeRunRecord) =
                oldItem.seq == newItem.seq
        }
    }
}