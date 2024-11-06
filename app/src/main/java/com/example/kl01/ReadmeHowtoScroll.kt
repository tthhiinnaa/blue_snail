package com.example.kl01

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_get_filelist_page.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_readme_howto_scroll.*

class ReadmeHowtoScroll : AppCompatActivity() {

    val TAG: String = "로그"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_readme_howto_scroll)


        textViewRead123.movementMethod = ScrollingMovementMethod()

        bt_scrolletc.setOnClickListener {
            val intent_openetc = Intent(this, MainEtcActivity::class.java)
            startActivity(intent_openetc)
        }
    }

    fun onBackButtonClicked(view: View) {
        Log.d(TAG, "ReadmeHowtoScroll - onBackButtonClicked() called")
        finish()
    }
}
