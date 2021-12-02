package com.hjadal.portalis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import com.hjadal.portalis.databinding.ActivityMainBinding
import okhttp3.*
import java.io.IOException
import okhttp3.Response


class MainActivity : AppCompatActivity() {

    private val client: OkHttpClient = OkHttpClient();
    lateinit var binding: ActivityMainBinding;

    @Throws(IOException::class)
    fun run(url: String, callback: Callback): Unit {
        val request: Request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.clickButton.setOnClickListener { this.userClicked() }
        setContentView(binding.root)
    }

    private fun userClicked() {
        run("http://hjaltesorgenfrei.dk", object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string();
                runOnUiThread {
                    binding.mainText.text = result
                    binding.mainText.movementMethod = ScrollingMovementMethod()
                }
            }
        })
    }
}