package com.woo.task.view.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.woo.task.R
import com.woo.task.databinding.ActivitySlideBinding
import com.woo.task.view.adapters.SlidePageAdapter
import com.woo.task.view.utils.AppPreferences

class SlideActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var name = ""
    private val vp by lazy {
        findViewById<ViewPager2>(R.id.slideVP)
    }
    val firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    private lateinit var switchButton: ViewPager2.OnPageChangeCallback
    private lateinit var binding: ActivitySlideBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppPreferences.setup(this)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, null)
        window.statusBarColor = ContextCompat.getColor(this, R.color.green)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
        auth = Firebase.auth
        val dotsSlide = binding.dotsIndicator
        val pagerAdapter = SlidePageAdapter(this) { name = getString(R.string.app_name) }
        vp.adapter = pagerAdapter
        dotsSlide.attachTo(vp)
        //Show button finish in Slide 4 and button next in another slide

        switchButton = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 3) {
                    binding.buttonFinish.visibility = View.VISIBLE
                    binding.buttonNext.visibility = View.GONE
                } else {
                    binding.buttonNext.visibility = View.VISIBLE
                    binding.buttonFinish.visibility = View.GONE
                }
                if(position > 0){
                    binding.buttonPrev.visibility = View.VISIBLE
                }else{
                    binding.buttonPrev.visibility = View.GONE
                }
            }
        }
        vp.registerOnPageChangeCallback(switchButton)
        //Button next used to swipe the slide until slide 4
        binding.buttonNext.setOnClickListener {
            if (vp.currentItem < 3) {
                vp.currentItem = vp.currentItem.plus(1)
            }
        }

        binding.buttonPrev.setOnClickListener {
            if (vp.currentItem > 0) {
                vp.currentItem = vp.currentItem.minus(1)
            }
        }
        //Button finish used to finish all
        binding.buttonFinish.setOnClickListener {
            AppPreferences.tutorial = "no"
            startActivity(Intent(this,MainActivity::class.java))
            finish()
            //overridePendingTransition(R.anim.slide_in_left,R.anim.slide_in_right,)
        }

        binding.btnSkip.setOnClickListener {
            AppPreferences.tutorial = "no"
            startActivity(Intent(this,MainActivity::class.java))
            finish()
            //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_in_left,)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        vp.registerOnPageChangeCallback(switchButton)
    }
}