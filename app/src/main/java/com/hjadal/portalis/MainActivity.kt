package com.hjadal.portalis

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hjadal.portalis.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.jsoup.Jsoup
import java.io.IOException


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.loadChapters()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun switchToChapterView(chapter: Chapter) {
        val intent = Intent(this, ReadChapterActivity::class.java).apply {
            putExtra("chapter", chapter)
        }
        startActivity(intent)
    }

    private fun loadChapters() {
        NetUtil.run("https://www.royalroad.com/fiction/22518/chrysalis", object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()
                val doc = Jsoup.parse(result as String)
                val elements = doc.getElementsByClass("chapter-row")
                val chapters = elements.toList()
                    .map { e ->
                        val title = e.getElementsByTag("a")[0].text()
                        val uri = "https://www.royalroad.com" + e.attr("data-url")
                        val number =
                            e.getElementsByAttribute("data-content")[0].attr("data-content")
                        Chapter(title, uri, number)
                    }
                runOnUiThread {
                    binding.listView.adapter = ChapterAdapter(chapters) { chapter ->
                        switchToChapterView(
                            chapter
                        )
                    }
                }
            }
        })
    }
}