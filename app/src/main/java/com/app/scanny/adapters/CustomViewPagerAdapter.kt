package com.app.scanny.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.scanny.fragments.HomeFragment
import com.app.scanny.fragments.ImageViewerFragment

class CustomViewPagerAdapter( fragment : Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment : Fragment

        when(position)
        {
            0 -> {
                fragment = HomeFragment()
            }

            1 -> {
                fragment = ImageViewerFragment()
            }

            else -> {
                fragment = HomeFragment()
            }
        }

        return fragment
    }




}