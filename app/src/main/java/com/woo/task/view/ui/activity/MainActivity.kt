package com.woo.task.view.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.woo.task.R
import com.woo.task.databinding.ActivityMainBinding
import com.woo.task.view.adapters.ViewPagerAdapter
import com.woo.task.view.utils.HorizontalMarginItemDecoration
import com.woo.task.viewmodel.TasksViewModel
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    val TAG = "FIREBASE"
    lateinit var binding: ActivityMainBinding
    private val tasksViewModel: TasksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        binding.taskView.adapter = adapter

        binding.tabIndicator.setViewPager2(binding.taskView)

        binding.taskView.clipToPadding = false
        binding.taskView.clipChildren = false
        binding.taskView.offscreenPageLimit = 1
        binding.taskView.setPadding(20, 0, 20, 0);
        binding.taskView.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx = resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            page.scaleY = 1 - (0.15f * abs(position))
        }
        binding.taskView.setPageTransformer(pageTransformer)

        val itemDecoration = HorizontalMarginItemDecoration(
            this,
            R.dimen.viewpager_next_item_visible
        )
        binding.taskView.addItemDecoration(itemDecoration)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            } else {
                val token = task.result
                Log.d(TAG, "Token Firebase: $token")
            }
        })

    }
}