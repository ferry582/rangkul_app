package com.example.rangkul.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.rangkul.R
import com.example.rangkul.databinding.ActivityToserviceBinding

class ToserviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityToserviceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToserviceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()

        val webview = findViewById<WebView>(R.id.wvToS)
        webview.settings.javaScriptEnabled

        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
        webview.loadUrl("https://www.app-privacy-policy.com/live.php?token=dwQENyTo2VvWdjeEvgO0D8fK7H9DjvAo")

    }

    private fun setToolbar() {
        setSupportActionBar(binding.settingToolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_grey)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Terms of Service"
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}