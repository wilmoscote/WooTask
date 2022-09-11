package com.woo.task.view.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.woo.task.view.ui.fragments.DoingFragment
import com.woo.task.view.ui.fragments.DoneFragment
import com.woo.task.view.ui.fragments.ToDoFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> ToDoFragment()
            1-> DoingFragment()
            2-> DoneFragment()
            else -> Fragment()
        }

    }

}