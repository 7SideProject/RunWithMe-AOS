package com.side.runwithme.view.recommend_detail.favorite

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseDialogFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.DialogFavoriteBinding

class FavoriteDialog : BaseDialogFragment<DialogFavoriteBinding>(R.layout.dialog_favorite) {

    override fun init() {

        initClickListener()

    }

    private fun initClickListener(){
        binding.apply {
            btnCancel.setOnClickListener {
                findNavController().popBackStack()
            }

            btnOk.setOnClickListener {

            }
        }
    }

}