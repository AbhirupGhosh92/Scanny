package com.app.scanny.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.app.scanny.R
import com.app.scanny.adapters.CustomViewPagerAdapter
import com.app.scanny.databinding.FragmentViewPagerBinding
import com.google.android.material.tabs.TabLayoutMediator
import java.io.Console

class ViewPagerFragment : Fragment() {


    private lateinit var databinding : FragmentViewPagerBinding
    private lateinit var adapter : CustomViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        Log.d("ViewPager","View Pager Fragment")
        databinding = DataBindingUtil.inflate(inflater,R.layout.fragment_view_pager,container,false)
        adapter = CustomViewPagerAdapter(this)
        databinding.vpHomer.adapter = adapter

        TabLayoutMediator(databinding.tabLayout, databinding.vpHomer) { tab, position ->

            var text = ""

            when(position)
            {
                0 -> {
                    text = "Home"
                }

                1 -> {
                    text = "Images"
                }
            }


            tab.text = text


        }.attach()
        return databinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }


}