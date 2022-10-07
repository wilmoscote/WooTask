package com.woo.task.view.ui.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.gms.ads.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.woo.task.databinding.ActivityMainBinding
import com.woo.task.BuildConfig
import com.woo.task.R
import com.woo.task.view.adapters.ViewPagerAdapter
import com.woo.task.view.utils.AppPreferences
import com.woo.task.view.utils.HorizontalMarginItemDecoration
import com.woo.task.viewmodel.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val TAG = "TASKDEBUG"
    lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private val tasksViewModel: TasksViewModel by viewModels()
    lateinit var toggle: ActionBarDrawerToggle
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppPreferences.setup(this)

        initAds()

        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val appVersion = Firebase.remoteConfig.getDouble("appversion")
                    Log.d(TAG,appVersion.toString())
                    if (appVersion.toInt() != BuildConfig.VERSION_CODE){
                       forceUpdate()
                    }
                }
            }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            AppPreferences.msg_token_fmt = token
            Log.d(TAG, AppPreferences.msg_token_fmt.toString())
            //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })

        auth = Firebase.auth
        if (auth.currentUser == null){
            newUserLogin()
            Log.d(TAG,"NO SESSION")
        }else{
            Log.d(TAG,"GOT SESSION")
        }

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
                        //Toast.makeText(this@MainActivity, "Home",Toast.LENGTH_SHORT).show()
                        binding.drawerLayout.close()
                    }
                    R.id.config -> {
                        startActivity(Intent(this@MainActivity,ConfigActivity::class.java))
                    }
                    R.id.shareApp -> {
                        try {
                            val bundle = Bundle()
                            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "SHARE_APP")
                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle)
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "text/plain"
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                            var shareMessage = "\n ${getString(R.string.share_app_text)} \n\n"
                            shareMessage =
                                """
                                ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                                
                                
                                """.trimIndent()
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                            startActivity(Intent.createChooser(shareIntent, getString(R.string.choose_one_option)))
                        } catch (e: Exception) {
                            //e.toString();
                        }
                    }

                    R.id.help -> {
                        startActivity(Intent(this@MainActivity,SlideActivity::class.java))
                        finish()
                        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_in_right)
                    }

                    R.id.logout -> {
                        MaterialAlertDialogBuilder(this@MainActivity)
                            .setTitle(resources.getString(R.string.logout_title))
                            .setMessage(getString(R.string.logout_message))
                            .setPositiveButton(resources.getString(R.string.dialog_confirm)){_,_->
                                Firebase.auth.signOut()
                                startActivity(Intent(this@MainActivity,LoginActivity::class.java))
                                finish()
                            }
                            .setNegativeButton(resources.getString(R.string.dialog_cancel)){dialog,_->
                                dialog.dismiss()
                            }
                            .show()
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
            binding.taskView.offscreenPageLimit = 2
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
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.open()
        }
        if(AppPreferences.bgColor!! != 0)  binding.mainLayout.setBackgroundColor(AppPreferences.bgColor!!)

    }

    private fun forceUpdate() {
        val alertadd = androidx.appcompat.app.AlertDialog.Builder(this)
        val factory = LayoutInflater.from(this)
        val view: View = factory.inflate(R.layout.update_dialog, null)
        alertadd.setView(view)
        alertadd.setCancelable(true)
        alertadd.setPositiveButton(
            getString(R.string.but_update)
        ) { dlg, _ ->
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
            finish()
        }
        alertadd.show()
    }

    private fun initAds() {
        MobileAds.initialize(this) {}

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        binding.adView.adListener = object: AdListener() {
            override fun onAdClicked() {

                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            override fun onAdLoaded() {
                binding.adView.isVisible = true
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun newUserLogin(){
        CoroutineScope(Dispatchers.IO).launch {
            auth.signInAnonymously()
                .addOnCompleteListener(this@MainActivity) { task ->
                    if (task.isSuccessful) {
                        val bundle = Bundle()
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "NEW_USER_LOGIN")
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                        // binding.pgBar.visibility = View.INVISIBLE
                        Log.d(TAG, "signInAnonymously:success")
                        //val user = auth.currentUser
                        //finish()
                    } else {
                        //binding.pgBar.visibility = View.INVISIBLE
                        Log.w(TAG, "signInAnonymously:failure", task.exception)
                    }
                }
        }
    }

    override fun onResume() {
        super.onResume()
        if(AppPreferences.bgColor!! != 0)  binding.mainLayout.setBackgroundColor(AppPreferences.bgColor!!)
    }

}