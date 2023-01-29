package com.example.rangkul.ui.post

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.rangkul.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// This is a Bottom Sheet Dialog Fragment for handle action in post options
class PostOptionsBottomSheetFragment : BottomSheetDialogFragment(), View.OnClickListener {
    private var mListener: ItemClickListener? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_bottom_post_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.tvSeeAccount).setOnClickListener(this)
        view.findViewById<View>(R.id.tvFollow).setOnClickListener(this)
        view.findViewById<View>(R.id.tvReportPost).setOnClickListener(this)
        view.findViewById<View>(R.id.tvLikeCountOptions).setOnClickListener(this)
        view.findViewById<View>(R.id.tvCommentOptions).setOnClickListener(this)
        view.findViewById<View>(R.id.tvDeletePost).setOnClickListener(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is ItemClickListener) {
            context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement ItemClickListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onClick(view: View) {
        val tvSelected = view as TextView
        mListener!!.onItemClick(tvSelected.text.toString())
        dismiss()
    }

    interface ItemClickListener {
        fun onItemClick(item: String?)
    }

    companion object {
        const val TAG = "ActionBottomDialog"
        fun newInstance(): PostOptionsBottomSheetFragment {
            return PostOptionsBottomSheetFragment()
        }
    }
}