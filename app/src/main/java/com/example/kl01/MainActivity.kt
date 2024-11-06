package com.example.kl01

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.os.Build
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_get_filelist_page.*
import kotlinx.android.synthetic.main.activity_main.bt_delete_cache
import java.io.FilenameFilter
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    //v0.006
    val TAG: String = "로그"

    var date = ""
    var currentdate: String = "실행 아직 안 됨"
    var date_simple = ""

    //파일 저장위치
    val dirfilestring : String = "./mnt/sdcard/Android/kl01files/"
    val dirfiles = File(dirfilestring)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "MainActivity - onCreate() called")

        getTimeNow()

        if (!dirfiles.exists()) {
            dirfiles.mkdirs()
        }

        //textView3.setMovementMethod(ScrollingMovementMethod());
        textView3.movementMethod = ScrollingMovementMethod()

        val picker = findViewById(R.id.timePicker) as TimePicker
        picker.setIs24HourView(true)


        // 앞서 설정한 값으로 보여주기
        // 없으면 디폴트 값은 현재시간
        val sharedPreferences = getSharedPreferences("daily alarm", Context.MODE_PRIVATE)
        val millis =
            sharedPreferences.getLong("nextNotifyTime", Calendar.getInstance().timeInMillis)

        val nextNotifyTime = GregorianCalendar()
        nextNotifyTime.timeInMillis = millis

        val nextDate = nextNotifyTime.time
        val date_text =
            SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(nextDate)
        Toast.makeText(
            applicationContext,
            "[처음 실행시] 다음 알람은 " + date_text + "으로 알람이 설정되었습니다!",
            Toast.LENGTH_SHORT
        ).show()
        textview_alarm.setText("현재" + date_text +"으로 알람이 설정되있습니다")


        // 이전 설정값으로 TimePicker 초기화
        val currentTime = nextNotifyTime.time
        val HourFormat = SimpleDateFormat("kk", Locale.getDefault())
        val MinuteFormat = SimpleDateFormat("mm", Locale.getDefault())

        val pre_hour = Integer.parseInt(HourFormat.format(currentTime))
        val pre_minute = Integer.parseInt(MinuteFormat.format(currentTime))


        if (Build.VERSION.SDK_INT >= 23) {
            picker.hour = pre_hour
            picker.minute = pre_minute
        } else {
            picker.currentHour = pre_hour
            picker.currentMinute = pre_minute
        }


        val button = findViewById(R.id.button_setAlarm) as Button
        button.setOnClickListener {
            val hour: Int
            val hour_24: Int
            val minute: Int
            val am_pm: String
            if (Build.VERSION.SDK_INT >= 23) {
                hour_24 = picker.hour
                minute = picker.minute
            } else {
                hour_24 = picker.currentHour
                minute = picker.currentMinute
            }
            if (hour_24 > 12) {
                am_pm = "PM"
                hour = hour_24 - 12
            } else {
                hour = hour_24
                am_pm = "AM"
            }

            // 현재 지정된 시간으로 알람 시간 설정
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, hour_24)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)

            // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1)
            }

            val currentDateTime = calendar.time
            val date_text =
                SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(
                    currentDateTime
                )
            Toast.makeText(applicationContext, date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT)
                .show()

            textview_alarm.setText( date_text +"으로 알람이 설정되었습니다")

            //  Preference에 설정한 값 저장
            val editor = getSharedPreferences("daily alarm", Context.MODE_PRIVATE).edit()
            editor.putLong("nextNotifyTime", calendar.timeInMillis)
            editor.apply()


            diaryNotification(calendar)
        }

        //버튼 이벤트들

        bt_delete_cache.setOnClickListener{
            deleteFolder()
            text_guide_1.setText("임시 메모리 삭제 완료")
            defaultSizeCheck()
        }

        bt_open_otherapp.setOnClickListener{
            text_guide_1.setText("카카오톡을 종료 후 열었습니다")
            try {
                val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                am.killBackgroundProcesses("com.kakao.talk")

                val intent = packageManager.getLaunchIntentForPackage("com.kakao.talk")
                intent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } catch (e: Exception) {
                val url = "market://details?id=$packageName"
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(i)
            }
            defaultSizeCheck()
        }

        bt_name_date_change.setOnClickListener {

            var date_rename = SimpleDateFormat("yyyyMMdd_hhmmss", Locale.getDefault()).format(Date())
            currentdate = date_rename

            var sizeoffolder = getFolderSizeGo() / 1024
            val file_default_check = File("./mnt/sdcard/Android/data/com.kakao.talk/cache/default")
            if (!file_default_check.exists()) {
                textView3.setText("default 폴더가 존재하지 않습니다. 카카오톡을 종료 한 뒤 카카오톡을 실행해주세요" )

            }
            else {
                text_guide_1.setText("파일명 현재 시간으로 변경 안됨")
                if (sizeoffolder < 200) {
                    textView3.setText("default폴더 크기 :" + sizeoffolder.toString() + "kbyte 사이즈가 너무 작네요. 비정상실행입니다. 카카오톡 종료후 재실행한뒤 화살표를 위아래로 반복해주세요")
                } else if (sizeoffolder < 1024) {
                    textView3.setText("default폴더 크기 :" + sizeoffolder.toString() + "kbyte 사이즈가 작네요. 비정상실행입니다. 카카오톡 실행 후 화살표를 충분히 위아래로 반복해주세요. default폴더 용량이 증가하지 않는다면 카카오톡을 종료 후 재실행해주세요.")
                } else if (sizeoffolder > 30720) {
                    textView3.setText("default폴더 크기 :" + sizeoffolder.toString() + "kbyte 임시 메모리 초기화가 필요해 보여요")
                } else {
                    renameFile(
                        "./mnt/sdcard/Android/data/com.kakao.talk/cache/default",
                        dirfilestring + currentdate
                    )
                    text_guide_1.setText(currentdate+"로 변경 완료")
                    textView3.setText("default폴더 크기 :" + sizeoffolder.toString() + "kbyte 충분히 큰 숫자라면 정상실행입니다")
                }
            }

           /*var fileSize_default_real = File("./mnt/sdcard/Android/data/com.kakao.talk/cache/default").length()
            textView3.setText(fileSize_default_real.toString())
            if(fileSize_default_real < 8000) {
                text_guide_1.setText("카카오톡을 종료후 다시 실행하신 뒤 스크롤 버튼을 눌러주세요")
            }
            else {
                renameFile(
                    "./mnt/sdcard/Android/data/com.kakao.talk/cache/default",
                    "./mnt/sdcard/Android/data/com.kakao.talk/cache/" + currentdate
                )
                text_guide_1.setText("파일명 현재 시간으로 변경 완료")
            }*/
        }

        bt_name_date_changed_check.setOnClickListener {
            fileNameChangedCheck()
            defaultSizeCheck()
        }

        bt_readmeopen.setOnClickListener {
            Log.d(TAG, "MainActivity - onCreate() called")
            val intent2 = Intent(this, Main2Activity::class.java)
            startActivity(intent2)
        }

        bt_getFileList.setOnClickListener {
            Log.d(TAG, "MainActivity - onCreate() called")
            val intent_alarmpage = Intent(this, GetFileListPAge::class.java)
            startActivity(intent_alarmpage)
        }

        bt_setFriendNum.setOnClickListener {
            val intentCD = Intent(this, CustomDlg::class.java)
            startActivity(intentCD)
        }

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "MainActivity - onStart() called")
        defaultSizeCheck()
        getTimeNow()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity - onResume() called")
        defaultSizeCheck()
        getTimeNow()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "MainActivity - onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "MainActivity - onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity - onDestroy() called")
    }

    fun deleteFolder(directoryName: String = ""): Boolean {
        var folder = directoryName

        if (folder.isNullOrBlank()) folder = "./mnt/sdcard/Android/data/com.kakao.talk/cache/default"


        var file = File(folder)
        try {
            var babyFileList = file.listFiles()
//재귀로 자식파일까지 삭제
            for (babyFile in babyFileList) {
                if (babyFile.isDirectory()) {
                    deleteFolder(babyFile.getAbsolutePath())
                } else {
                    babyFile.delete()
                }
            }
            file.delete()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    //폴더의 총 용량 계산
    fun getFolderSizeGo(directoryName: String = ""): Long {
        var folder = directoryName
        var length: Long = 0
        if (folder.isNullOrBlank()) folder = "./mnt/sdcard/Android/data/com.kakao.talk/cache/default"


        var file = File(folder)
        try {
            var length: Long = 0
            val files = file.listFiles()
            val count = files.size

            for (i in 0 until count) {
                if (files[i].isFile()) {
                    length += files[i].length()
                } else {
                    length += getFolderSizeGo(files[i].toString())
                }
            }
            return length
        }catch (e: Exception) {
            return 0
        }
    }

    fun renameFile(filename: String, newFilename: String) {
        val file = File(filename)
        val fileNew = File(newFilename)
        if (file.exists()) file.renameTo(fileNew)

    }

    fun fileNameChangedCheck() {
        /*var file2 = File("./mnt/sdcard/Android/data/com.kakao.talk/cache/"+ currentdate)

        if (file2.exists()) {
            text_file.setText(file2.toString()+"존재yes")
        }
        else {
            text_file.setText("변경존재no")
        }
        */
        checkTodayFile()
    }

    fun defaultSizeCheck(){
        var sizeoffolder = getFolderSizeGo() / 1024
        val file_default_check = File("./mnt/sdcard/Android/data/com.kakao.talk/cache/default")
        if (!file_default_check.exists()) {
            textView3.setText("default 폴더가 존재하지 않습니다 카카오톡을 종료 한 뒤 카카오톡을 실행해주세요")
        }
        else {
            if (sizeoffolder < 200) {
                textView3.setText("default폴더 크기 :" + sizeoffolder.toString() + "kbyte 사이즈가 너무 작네요. 비정상실행입니다. 카카오톡 종료후 재실행한뒤 화살표를 위아래로 반복해주세요")
            } else if (sizeoffolder < 1024) {
                textView3.setText("default폴더 크기 :" + sizeoffolder.toString() + "kbyte 사이즈가 작네요. 비정상실행입니다. 카카오톡 종료후 재실행한뒤 화살표를 충분히 위아래로 반복해주세요")
            } else if (sizeoffolder > 60720) {
                textView3.setText("default폴더 크기 :" + sizeoffolder.toString() + "kbyte 임시 메모리 초기화가 필요해 보여요")
            } else {
                textView3.setText("default폴더 크기 :" + sizeoffolder.toString() + "kbyte 충분히 큰 숫자라면 정상실행입니다, 설명서를 참조해주세요")
            }
        }

        //textView3.setText(getFolderSizeGo().toString())

        /*val file_default_check = File("./mnt/sdcard/Android/data/com.kakao.talk/cache")
        var sizeoffile : Long? = file_default_check.length()
        textView3.setText(sizeoffile.toString())*/
        /*var file_default_check = File("./mnt/sdcard/Android/data/com.kakao.talk/cache/default")
        if (!file_default_check.exists()) {
            textView3.setText("default 폴더가 존재하지 않습니다 카카오톡을 종료 한 뒤 카카오톡을 실행해주세요")
        }
        else{
            var fileSize_default_size = file_default_check.length()/1024
            if(fileSize_default_size<1)
            {
                textView3.setText("default폴더 크기 :" + fileSize_default_size.toString() + "kbyte 사이즈가 너무 작네요. 비정상실행입니다. 카카오톡 종료후 재실행한뒤 화살표를 위아래로 반복해주세요")
            }
            else if(fileSize_default_size < 6) {
                textView3.setText("default폴더 크기 :" + fileSize_default_size.toString() + "kbyte 사이즈가 너무 작네요. 비정상실행입니다. 카카오톡 종료후 재실행한뒤 화살표를 위아래로 반복해주세요")
            }
            else if(fileSize_default_size < 100) {
                textView3.setText("default폴더 크기 :" + fileSize_default_size.toString() + "kbyte 충분히 큰 숫자라면 정상실행입니다")
            }
            else {
                textView3.setText("default폴더 크기 :" + fileSize_default_size.toString() + "kbyte 임시 메모리 초기화가 필요해 보여요")
            }
        }*/
    }

  /*  fun fileListCheck(){
        var file_cachecheck= File("./mnt/sdcard/Android/data/com.kakao.talk/cache/")
        //var file_changedcehck = File("./mnt/sdcard/Android/data/com.kakao.talk/cache/"+ currentdate)

        var filelist_array  = file_cachecheck.list()
        var filesizenumber = filelist_array.size
        var filelist_strtxt : String? = ""
        for (i in 0..filesizenumber-1) {

            filelist_strtxt = filelist_strtxt + " " + filelist_array[i]
        }
        textViewFileList.setText(filelist_strtxt)
        val filelist_str = ""

        //file_changedcehck.list(file_changedcehck,date_simple)
        //file_changedcehck.listFiles((file_changedcehck, date_simple) -> name.toLowerCase().endsWith(".txt"));
        //val fielArray = file_changedcehck.listFiles(file_changedcehck )
       // val fileArray = file_changedcehck.listFiles { file ->
      //      file.length() > 0 && file.name.matches(fileMatcherRegex)
       // }
    }
*/


    /*fun fileStasusCheck(){
        val f = File("./mnt/sdcard/Android/data/com.kakao.talk/cache/default")
        if (f.exists()) {
            val len = f.length()
            println("file Size : $len 입니다.")
        } else {
            println("파일 없음")
        }

    }*/


    internal fun diaryNotification(calendar: Calendar) {
        //        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        //        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //        Boolean dailyNotify = sharedPref.getBoolean(SettingsActivity.KEY_PREF_DAILY_NOTIFICATION, true);
        val dailyNotify = true // 무조건 알람을 사용

        val pm = this.packageManager
        val receiver = ComponentName(this, DeviceBootReceiver::class.java!!)
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var alarmMgr: AlarmManager? = null
        lateinit var alarmIntent2: PendingIntent
        alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent2 = Intent(this, AlarmReceiver_week::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 1, intent, 0)
        }

        // Set the alarm to start at 8:30 a.m.
        val calendar2: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 17)
            set(Calendar.MINUTE, 40)
        }

        // setRepeating() lets you specify a precise custom interval--in this case,
        // 20 minutes.


        // 사용자가 매일 알람을 허용했다면
        if (dailyNotify) {


            if (alarmManager != null) {

                /*alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY, pendingIntent
                )*/
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            }

            if (alarmMgr != null)
                alarmMgr?.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY*7,
                    alarmIntent2
                )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmMgr?.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

        }
        //        else { //Disable Daily Notifications
        //            if (PendingIntent.getBroadcast(this, 0, alarmIntent, 0) != null && alarmManager != null) {
        //                alarmManager.cancel(pendingIntent);
        //                //Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show();
        //            }
        //            pm.setComponentEnabledSetting(receiver,
        //                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        //                    PackageManager.DONT_KILL_APP);
        //        }



    }



    /*fun renamefileGo(){
        renameFile(
            "./mnt/sdcard/Android/data/com.kakao.talk/cache/default",
            "./mnt/sdcard/Android/data/com.kakao.talk/cache/" + currentdate
        )
        text_guide_1.setText("파일명 현재 시간으로 변경 완료")
    }*/

    fun getTimeNow(){
        date = SimpleDateFormat("yyyyMMdd_hhmmss", Locale.getDefault()).format(Date())
        date_simple = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    }

    fun checkTodayFile(){
        var file_cachecheck = File(dirfilestring)
        //var file_changedcehck = File("./mnt/sdcard/Android/data/com.kakao.talk/cache/"+ currentdate)
        getTimeNow()

        var filelists = file_cachecheck.list(object : FilenameFilter {
            override fun accept(dir: File?, filename: String): Boolean {
                if (filename.startsWith(date_simple))
                    return true
                else
                    return false
            }
        })

        var filesizenumber = filelists.size
        if (filesizenumber == 0){
            text_file.setText("오늘 생성된 폴더 목록이 없습니다")
        }
        else {
            var filelist_strtxt: String? = ""
            for (i in 0..filesizenumber - 1) {

                filelist_strtxt = filelist_strtxt + ", " + filelists[i]
            }
            text_file.setText("오늘 생성된 폴더 목록 : " + filelist_strtxt)
        }

    }

}





