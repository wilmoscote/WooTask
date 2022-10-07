package com.woo.task.view.ui.activities

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.woo.task.databinding.ActivityWebViewBinding


class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.settings.javaScriptEnabled = true
        val pdf = "https://firebasestorage.googleapis.com/v0/b/woo-task.appspot.com/o/WOO%20TASK%20PRIVACY%20POLICY.pdf?alt=media&token=584151d9-5d9a-4c20-a0eb-da9cbd67a447"
        binding.webView.loadUrl("http://docs.google.com/gview?embedded=true&url=$pdf")
    }
}