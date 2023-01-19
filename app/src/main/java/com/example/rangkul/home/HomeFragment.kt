package com.example.rangkul.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rangkul.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set Up Recycler View
        val postDataList = ArrayList<PostData>()

        for (i in 1..5) {
            postDataList.add(PostData("Della Delila","Category","3 hours ago",
                "Overwhelmed banget sama tugas kuliah ku.."))
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_post)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = true
        recyclerView.adapter = PostAdapter(requireContext(), postDataList) {

        }

    }

}