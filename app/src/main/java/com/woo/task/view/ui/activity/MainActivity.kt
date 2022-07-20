package com.woo.task.view.ui.activity

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.woo.task.R
import com.woo.task.databinding.ActivityMainBinding
import com.woo.task.model.interfaces.RecyclerViewInterface
import com.woo.task.model.room.Task
import com.woo.task.model.room.TaskApp
import com.woo.task.model.room.TaskDb
import com.woo.task.view.adapters.TaskAdapter
import com.woo.task.view.adapters.ViewPagerAdapter
import com.woo.task.view.utils.HorizontalMarginItemDecoration
import com.woo.task.viewmodel.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import kotlin.math.abs

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val TAG = "FIREBASE"
    lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private val tasksViewModel: TasksViewModel by viewModels()
    lateinit var app : TaskDb
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        app = Room
            .databaseBuilder(this, TaskDb::class.java,"task")
            .build()
        val hView = binding.navView.getHeaderView(0)
        val foto = hView.findViewById<ImageView>(R.id.foto)
        val user = hView.findViewById<TextView>(R.id.user)
        val email = hView.findViewById<TextView>(R.id.email)
        Glide.with(applicationContext).load(auth.currentUser?.photoUrl).placeholder(R.drawable.default_profile).circleCrop().into(foto)
        user.text = if(!auth.currentUser?.displayName.isNullOrEmpty()) auth.currentUser?.displayName else getString(R.string.no_session_user)
        email.text = auth.currentUser?.email

        tasksViewModel.checkCloudTasks()

        lifecycleScope.launch {
            toggle = ActionBarDrawerToggle(this@MainActivity,binding.drawerLayout,R.string.abrir,R.string.cerrar)
            binding.drawerLayout.addDrawerListener(toggle)

            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            binding.navView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.home -> {
                        Toast.makeText(this@MainActivity, "Home",Toast.LENGTH_SHORT).show()
                    }
                    R.id.config -> {
                        startActivity(Intent(this@MainActivity,ConfigActivity::class.java))
                    }
                }
                true
            }

            tasksViewModel.onCreate()
            val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
            binding.taskView.adapter = adapter

            binding.tabIndicator.attachTo(binding.taskView)

            binding.taskView.clipToPadding = false
            binding.taskView.clipChildren = false
            binding.taskView.offscreenPageLimit = 1
            binding.taskView.setPadding(20, 0, 20, 0)
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
                this@MainActivity,
                R.dimen.viewpager_next_item_visible
            )
            binding.taskView.addItemDecoration(itemDecoration)
        }

        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.logout_title))
                .setMessage(getString(R.string.logout_message))
                .setPositiveButton(resources.getString(R.string.dialog_confirm)){_,_->
                    Firebase.auth.signOut()
                    startActivity(Intent(this,LoginActivity::class.java))
                    finish()
                }
                .setNegativeButton(resources.getString(R.string.dialog_cancel)){dialog,_->
                    dialog.dismiss()
                }
                .show()
        }
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.open()
        }

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        tasksViewModel.onCreate()
    }

}