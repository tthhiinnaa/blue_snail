package com.example.kl01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_get_filelist_page.*
import kotlinx.android.synthetic.main.activity_main2.*
import java.io.File
import java.io.FilenameFilter

class GetFileListPAge : AppCompatActivity() {

    val TAG: String = "로그"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_filelist_page)

        textViewFileList.movementMethod = ScrollingMovementMethod()

        bt__getCacheFileList.setOnClickListener {
            fileListCheck()
        }
    }

    fun onBackButtonClicked(view: View) {
        Log.d(TAG, "GetFileListPAge - onBackButtonClicked() called")
        finish()
    }

    fun fileListCheck() {
        //파일 저장 위치
        val dirfilestring : String = "./mnt/sdcard/Android/kl01files/"
        var file_cachecheck = File(dirfilestring)
        //var file_changedcehck = File("./mnt/sdcard/Android/data/com.kakao.talk/cache/"+ currentdate)

        var filelists = file_cachecheck.list(object : FilenameFilter {
            override fun accept(dir: File?, filename: String): Boolean {
                if (filename.startsWith("2020"))
                    return true
                else
                    return false
            }
        })
        var filesizenumber = filelists.size
        var filelist_strtxt: String? = ""
        for (i in 0..filesizenumber - 1) {

            filelist_strtxt = filelist_strtxt + ", " + filelists[i]
        }
        textViewFileList.setText("cache 1차 하위 폴더 목록 중 날짜별로 정리 된 폴더 목록 : " + filelist_strtxt)
       /* var filelist_array = file_cachecheck.list()
        var filesizenumber = filelist_array.size
        var filelist_strtxt: String? = ""
        for (i in 0..filesizenumber - 1) {

            filelist_strtxt = filelist_strtxt + ", " + filelist_array[i]
        }
        textViewFileList.setText("cache 1차 하위 폴더 목록 : " + filelist_strtxt)*/
    }

    fun fileGo(){
        val files = File("./mnt/sdcard/Android/data/com.kakao.talk/cache/").listFiles(object : FilenameFilter {
            override fun accept(dir: File?, filename: String): Boolean {
                if (filename.startsWith("2020"))
                    return true
                else
                    return false
            }
        })

    }
}
