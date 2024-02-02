package com.side.runwithme.view.challenge_board

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.side.domain.model.Board
import com.side.runwithme.databinding.ItemChallengeBoardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChallengeBoardAdapter (
    private val userSeq: Long,
    private val deleteBoardClickListener: DeleteBoardClickListener,
    private val reportBoardClickListener: ReportBoardClickListener,
    private val jwt: StateFlow<String>
) : PagingDataAdapter<Board, ChallengeBoardAdapter.ChallengeBoardViewHolder>(
    diffUtil
) {
    class ChallengeBoardViewHolder(val binding: ItemChallengeBoardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            userSeq: Long,
            board: Board,
            deleteBoardClickListener: DeleteBoardClickListener,
            reportBoardClickListener: ReportBoardClickListener
        ) {
            binding.apply {
                this.board = board

                this.imgReport.setOnClickListener {
                    reportBoardClickListener.onClick(board.boardSeq)
                }

                if (userSeq != board.userSeq) {
                    imgDelete.visibility = View.GONE
                } else {
                    imgDelete.visibility = View.VISIBLE
                    this.imgDelete.setOnClickListener {
                        deleteBoardClickListener.onClick(board.boardSeq)
                    }
                }
            }
        }

        fun bindJwt(jwt: String){
            binding.apply {
                this.jwt = jwt
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeBoardViewHolder {
        val binding =
            ItemChallengeBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChallengeBoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChallengeBoardViewHolder, position: Int) {
        val item = getItem(position)

        if (item != null) {
            holder.bind(userSeq, item, deleteBoardClickListener, reportBoardClickListener)
        }

        CoroutineScope(Dispatchers.IO).launch {
            jwt.collectLatest {jwt ->
                if(item != null){
                    holder.bindJwt(jwt)
                }
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Board>() {
            override fun areContentsTheSame(oldItem: Board, newItem: Board) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Board, newItem: Board) =
                oldItem.boardSeq == newItem.boardSeq
        }
    }
}