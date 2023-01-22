package com.example.rangkul.ui.profile

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.rangkul.R
import com.example.rangkul.databinding.ActivityPolicyBinding

class PolicyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPolicyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()

        val webview = findViewById<WebView>(R.id.wvPolicy)
        webview.settings.javaScriptEnabled

        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
        webview.loadUrl("https://www.app-privacy-policy.com/live.php?token=pizKgswNU6mlPfqGaWR71mmtce7EeOZm")
    }

    private fun setToolbar() {
        setSupportActionBar(binding.settingToolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_grey)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Privacy Policy"
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}