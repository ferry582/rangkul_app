package com.example.rangkul.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rangkul.R
import com.example.rangkul.data.model.CategoryListData
import com.example.rangkul.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    lateinit var categoryList: ArrayList<CategoryListData>
    lateinit var categoryListAdapter: CategoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configure Post RecyclerView
        binding.rvCategoryList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCategoryList.setHasFixedSize(true)
        binding.rvCategoryList.isNestedScrollingEnabled = false

        categoryList = ArrayList()
        addCategoryListData()

        categoryListAdapter =
            CategoryListAdapter(
                categoryList,
                onItemClicked = { pos, item ->
                    val intent = Intent(requireContext(), CategoryContentActivity::class.java)
                    intent.putExtra("CATEGORY", item.name)
                    startActivity(intent)
                }
            )

        binding.rvCategoryList.adapter = categoryListAdapter
    }

    private fun addCategoryListData() {
        categoryList.add(CategoryListData(R.drawable.il_category_mentalhealth, "Mental Health"))
        categoryList.add(CategoryListData(R.drawable.il_category_relationship, "Relationship"))
        categoryList.add(CategoryListData(R.drawable.il_category_family, "Family"))
        categoryList.add(CategoryListData(R.drawable.il_category_health, "Health"))
        categoryList.add(CategoryListData(R.drawable.il_category_abuse, "Abuse"))
        categoryList.add(CategoryListData(R.drawable.il_cateogry_bullying, "Bullying"))
        categoryList.add(CategoryListData(R.drawable.il_category_sara, "Sara"))
        categoryList.add(CategoryListData(R.drawable.il_category_depression, "Depression"))
        categoryList.add(CategoryListData(R.drawable.il_category_harassment, "Harassment"))
        categoryList.add(CategoryListData(R.drawable.il_category_addictioin, "Addiction"))
        categoryList.add(CategoryListData(R.drawable.il_category_work, "Work"))
        categoryList.add(CategoryListData(R.drawable.il_category_education, "Education"))
        categoryList.add(CategoryListData(R.drawable.il_category_personality, "Personality"))
        categoryList.add(CategoryListData(R.drawable.il_category_bodyshaming, "Body Shaming"))
        categoryList.add(CategoryListData(R.drawable.il_category_anxiety, "Anxiety"))
        categoryList.add(CategoryListData(R.drawable.il_category_friends, "Friends"))
        categoryList.add(CategoryListData(R.drawable.il_category_traumatic, "Traumatic"))
        categoryList.add(CategoryListData(R.drawable.il_category_financial, "Financial"))
        categoryList.add(CategoryListData(R.drawable.il_category_selfharm, "Self-harm"))
        categoryList.add(CategoryListData(R.drawable.il_category_discrimination, "Discrimination"))
    }
}