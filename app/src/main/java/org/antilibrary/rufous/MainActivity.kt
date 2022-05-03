package org.antilibrary.rufous

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import org.antilibrary.rufous.databinding.ActivityMainBinding
import java.net.URISyntaxException
import java.security.KeyStore

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.webView.apply {

            settings.apply {
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                domStorageEnabled = true
                setSupportMultipleWindows(true)
                useWideViewPort = true
                loadWithOverviewMode = true
                builtInZoomControls = true
                setSupportZoom(true)
                displayZoomControls = true
            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    if (url != null && url.startsWith("intent://")) {
                        try {
                            val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                            val existPackage =
                                packageManager.getLaunchIntentForPackage(intent.getPackage()!!)
                            if (existPackage != null) {
                                startActivity(intent)
                            } else {
                                val marketIntent = Intent(Intent.ACTION_VIEW)
                                marketIntent.data =
                                    Uri.parse("market://details?id=" + intent.getPackage()!!)
                                startActivity(marketIntent)
                            }
                            return true
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else if (url != null && url.startsWith("market://")) {
                        try {
                            val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                            if (intent != null) {
                                startActivity(intent)
                            }
                            return true
                        } catch (e: URISyntaxException) {
                            e.printStackTrace()
                        }

                    }
                    webChromeClient = WebChromeClient()
                    // 주소 로딩
                    view.loadUrl(url)
                    return false
                }
                // 페이지 접속 마치면 호출되는 메소드
                override fun onPageFinished(view: WebView, url: String) {
                    binding.urlEditText.setText(url)
                }
            }
        }



        binding.webView.loadUrl("http://www.kais.co.kr")

        binding.urlEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.webView.loadUrl(binding.urlEditText.text.toString())
                true
            } else {
                false
            }
        }

        registerForContextMenu(binding.webView)

    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }



}