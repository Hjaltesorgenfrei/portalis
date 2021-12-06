package com.hjadal.portalis

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.jsoup.Jsoup
import java.io.IOException

class ReadChapterActivity : AppCompatActivity() {
    private lateinit var chapter: Chapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chapter = intent.getSerializableExtra("chapter") as Chapter

        /*
        binding = ActivityReadChapterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = chapter.title
        binding.text.movementMethod = ScrollingMovementMethod()
        */
        downloadChapter()
    }

    private fun downloadChapter() {
        NetUtil.run(chapter.uri, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()
                val doc = Jsoup.parse(result as String)
                val chapterHtml = doc.getElementsByClass("chapter-content")[0].html()
                val chapterText =
                    HtmlCompat.fromHtml(chapterHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)
                runOnUiThread {
                }
            }
        })
    }
}