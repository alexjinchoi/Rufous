package org.antilibrary.rufous
import org.antilibrary.rufous.bookmark.Bookmark
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import org.antilibrary.rufous.databinding.ActivityMainBinding
import java.net.URISyntaxException
import browse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = AppDatabase.getInstance(applicationContext)!!

//      refreshBookmarkList()

        binding.addBookmark.setOnClickListener {
            addBookmark()
        }

        binding.removeBookmark.setOnClickListener {
            deleteBookmark()
        }

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
                    if ( url.startsWith("intent://")) {
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

                    } else if ( url.startsWith("market://")) {
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
                    view.loadUrl(url)
                    return false
                }
                override fun onPageFinished(view: WebView, url: String) {
                    binding.urlEditText.setText(url)
                }
            }
        }

        binding.goBack.setOnClickListener {
            binding.webView.goBack()
        }

        binding.goForward.setOnClickListener {
            binding.webView.goForward()
        }

        binding.refresh.setOnClickListener {
            binding.webView.reload()
        }


        if (intent.hasExtra("siteUrl")) {
            val siteUrl = intent.getStringExtra("siteUrl")
            binding.webView.loadUrl(siteUrl.toString())
        } else {
            binding.webView.loadUrl("https://www.google.com")
        }

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

    private fun addBookmark(){
        val siteName = binding.webView.title.toString()
        val siteUrl = binding.urlEditText.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            db.bookmarkDao().insert(
                Bookmark(siteName,siteUrl)
            )
//           refreshBookmarkList()
            val intent = Intent(this@MainActivity, BookmarkActivity::class.java)
            startActivity(intent)
        }
    }

    private fun deleteBookmark(){
        val siteUrl = binding.urlEditText.text.toString()

        CoroutineScope(Dispatchers.Main).launch {
            val delete = async(Dispatchers.IO) {
                db.bookmarkDao().deleteBookmarkBySiteUrl(siteUrl)
            }
            delete.await()
//          refreshBookmarkList()
            val intent = Intent(this@MainActivity, BookmarkActivity::class.java)
            startActivity(intent)
        }
    }

/*    private fun deleteAllBookmarks(){
        CoroutineScope(Dispatchers.Main).launch {
            val delete = async(Dispatchers.IO) {
                db.bookmarkDao().deleteAll()
            }
            delete.await()
            refreshBookmarkList()
        }
    }*/

/*    private fun refreshBookmarkList(){
        var bookmarkList = ""

        CoroutineScope(Dispatchers.Main).launch {
            val bookmarks = CoroutineScope(Dispatchers.IO).async {
                db.bookmarkDao().getAll()
            }.await()

            for(bookmark in bookmarks){
                bookmarkList += "${bookmark.id}:${bookmark.siteName}:${bookmark.siteUrl}</a>\n"
            }
            binding.bookmarkView.text = bookmarkList
        }
    }*/

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                binding.webView.loadUrl("https://www.google.com")
                return true
            }
            R.id.action_bookmarks -> {
                val intent = Intent(this, BookmarkActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_browser -> {
                binding.webView.url?.let { url ->
                    browse(url)
                }
                return true
            }
        }
        return super.onContextItemSelected(item)
    }



}