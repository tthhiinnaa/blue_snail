package com.example.kl01

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_readme_howto_scroll.*
import java.io.File
import java.time.LocalDateTime


class Main2Activity : AppCompatActivity() {

    val TAG: String = "로그"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        textViewEx2.movementMethod = ScrollingMovementMethod()

        bt_else.setOnClickListener {
            val intent_scrollapp = Intent(this, ReadmeHowtoScroll::class.java)
            startActivity(intent_scrollapp)
        }
    }

    fun onBackButtonClicked(view: View){
        Log.d(TAG, "Main2Activity - onBackButtonClicked() called")
        finish()
    }

    /*fun getTime(string: String){
        val current = LocalDateTime.now()

    }*/




    //button_time.setText("버튼 클릭 후" + "로 변경!")
}
