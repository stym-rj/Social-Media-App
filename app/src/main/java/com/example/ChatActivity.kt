package com.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.chatFragments.FragmentCalls
import com.example.chatFragments.FragmentChat
import com.example.chatFragments.FragmentStatus
import com.example.socialmediaapp.databinding.ActivityChatBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class ChatActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityChatBinding.inflate(layoutInflater)
    }

    private lateinit var onPageChangeCallback: OnPageChangeCallback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.viewPager.adapter = ViewpagerAdapter(supportFragmentManager, lifecycle)
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPager.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) { }

            override fun onTabReselected(tab: TabLayout.Tab?) { }
        })

        onPageChangeCallback = object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        }
        binding.viewPager.registerOnPageChangeCallback(onPageChangeCallback)

    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewPager.unregisterOnPageChangeCallback(onPageChangeCallback)
    }
}

class ViewpagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount() = 3     // b/c we have only 3 tabItems.

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FragmentChat()
            1 -> FragmentStatus()
            2 -> FragmentCalls()
            else -> FragmentChat()
        }
    }
}