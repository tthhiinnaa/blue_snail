package com.example.kl01;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver_week extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pending2 = PendingIntent.getActivity(context, 1,
                notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default2");


        //OREO API 26 이상에서는 채널 필요
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_stat_starb_notiicon); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남


            String channelName ="매주 알람 채널";
            String description = "매주 정해진 시간에 알람합니다.";
            int importance = NotificationManager.IMPORTANCE_HIGH; //소리와 알림메시지를 같이 보여줌

            NotificationChannel channel = new NotificationChannel("default2", channelName, importance);
            channel.setDescription(description);

            if (notificationManager != null) {
                // 노티피케이션 채널을 시스템에 등록
                notificationManager.createNotificationChannel(channel);
            }
        }else builder.setSmallIcon(R.mipmap.ic_launcher_starshine); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        builder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())

                .setContentTitle("카톡 친구수를 등록해주세요")
                .setContentText("일주일마다 메뉴에서 친구수를 등록해주세요")
                .setContentIntent(pending2);

        if (notificationManager != null) {

            // 노티피케이션 동작시킴
            notificationManager.notify(11133, builder.build());

            Calendar nextNotifyTime2 = Calendar.getInstance();

            // 다음주 같은 시간으로 알람시간 결정
            nextNotifyTime2.add(Calendar.DATE, 7);
           // nextNotifyTime2.add(Calendar.MINUTE, 30);
            //  Preference에 설정한 값 저장
            SharedPreferences.Editor editor2 = context.getSharedPreferences("weekly alarm", MODE_PRIVATE).edit();
            editor2.putLong("nextNotifyTime_week", nextNotifyTime2.getTimeInMillis());
            editor2.apply();

            Date currentDateTime = nextNotifyTime2.getTime();
            String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
            Toast.makeText(context.getApplicationContext(),"다음주 친구수 등록 " + date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();
        }
    }
}
