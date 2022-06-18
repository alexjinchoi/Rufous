package org.antilibrary.rufous
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.antilibrary.rufous.bookmark.Bookmark
import org.antilibrary.rufous.databinding.ActivityBookmarkBinding


class BookmarkActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityBookmarkBinding.inflate(layoutInflater)
    }
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnClose.setOnClickListener {
            finish()
        }

        val bookmarkItems = mutableListOf<Bookmark>()

        db = AppDatabase.getInstance(applicationContext)!!

        val savedBookmark = db.bookmarkDao().getAll()
        if(savedBookmark.isNotEmpty()){
            bookmarkItems.addAll(savedBookmark)
        }
        val listAdapter = ListAdapter(this, bookmarkItems)
        binding.bookmarkListView.adapter = listAdapter
        binding.bookmarkListView.choiceMode = ListView.CHOICE_MODE_SINGLE
        binding.bookmarkListView.dividerHeight = 0
        binding.bookmarkListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectItem = parent.getItemAtPosition(position) as Bookmark
            Toast.makeText(this, selectItem.siteUrl, Toast.LENGTH_SHORT).show()
            val detailIntent = Intent(this, MainActivity::class.java)
            detailIntent.putExtra("siteUrl", selectItem.siteUrl)
            startActivity(detailIntent)
            finish()
        }
    }

}
