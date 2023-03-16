package com.side.runwithme.view.crew

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.side.domain.model.Crew
import com.side.runwithme.databinding.ItemCrewListBinding

class CrewListAdapter() : ListAdapter<Crew, CrewListAdapter.CrewListViewHolder>(diffUtil) {

    inner class CrewListViewHolder(val binding: ItemCrewListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(crew: Crew) {
            binding.crew = crew
            binding.executePendingBindings()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrewListViewHolder {
        val binding =
            ItemCrewListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CrewListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CrewListViewHolder, position: Int) {
        val item = getItem(position)

        if (item != null) {
            holder.bind(item)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Crew>() {
            override fun areContentsTheSame(oldItem: Crew, newItem: Crew) =
                oldItem.hashCode() == newItem.hashCode()

            override fun areItemsTheSame(oldItem: Crew, newItem: Crew) =
                oldItem.seq == newItem.seq
        }
    }

}