package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomePagerAdapter(activity: AppCompatActivity) :
    FragmentStateAdapter(activity) {

    val limitsFragment = LimitsFragment()
    private val boardsFragment = BoardsFragment()

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            limitsFragment
        } else {
            boardsFragment
        }
    }
}
