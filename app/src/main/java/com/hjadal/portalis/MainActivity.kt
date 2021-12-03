package com.hjadal.portalis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import com.hjadal.portalis.databinding.ActivityMainBinding
import okhttp3.*
import java.io.IOException
import okhttp3.Response
import org.jsoup.Jsoup


class MainActivity : AppCompatActivity() {

    private val client: OkHttpClient = OkHttpClient()
    lateinit var binding: ActivityMainBinding

    @Throws(IOException::class)
    fun run(url: String, callback: Callback) {
        val request: Request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.loadChapters()

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.listView.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, _, position, _ ->
                println((adapterView.getItemAtPosition((position)) as Chapter).title)
            }
        setContentView(binding.root)
    }

    private fun loadChapters() {
        val context = this
        run("https://www.royalroad.com/fiction/22518/chrysalis", object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()
                val doc = Jsoup.parse(result as String)
                val elements = doc.getElementsByClass("chapter-row")
                val chapters = elements.toList()
                    .map {e -> Chapter(e.getElementsByTag("a")[0].text())}
                println(chapters)
                runOnUiThread {
                    binding.listView.adapter = ChapterAdapter(context, chapters)
                }
            }
        })
    }
}