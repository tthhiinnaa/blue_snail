package com.example.kl01;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {

            // on device boot complete, reset the alarm
            Intent alarmIntent = new Intent(context, com.example.kl01.AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//

            SharedPreferences sharedPreferences = context.getSharedPreferences("daily alarm", MODE_PRIVATE);
            long millis = sharedPreferences.getLong("nextNotifyTime", Calendar.getInstance().getTimeInMillis());


            Calendar current_calendar = Calendar.getInstance();
            Calendar nextNotifyTime = new GregorianCalendar();
            nextNotifyTime.setTimeInMillis(sharedPreferences.getLong("nextNotifyTime", millis));

            if (current_calendar.after(nextNotifyTime)) {
                nextNotifyTime.add(Calendar.DATE, 1);
            }

            Date currentDateTime = nextNotifyTime.getTime();
            String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
            Toast.makeText(context.getApplicationContext(),"[재부팅후] 다음 알람은 " + date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();


            if (manager != null) {
                manager.setRepeating(AlarmManager.RTC_WAKEUP, nextNotifyTime.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            }

            // on device boot complete, reset the alarm
            Intent alarmIntent_week = new Intent(context, com.example.kl01.AlarmReceiver_week.class);
            PendingIntent pendingIntent_week = PendingIntent.getBroadcast(context, 1, alarmIntent_week, 0);

            AlarmManager manager_week = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//

            SharedPreferences sharedPreferences_week = context.getSharedPreferences("weekly alarm", MODE_PRIVATE);
            long millis_week = sharedPreferences_week.getLong("nextNotifyTime_week", Calendar.getInstance().getTimeInMillis());


            Calendar current_calendar2 = Calendar.getInstance();
            Calendar nextNotifyTime_week = new GregorianCalendar();
            nextNotifyTime_week.setTimeInMillis(sharedPreferences_week.getLong("nextNotifyTime_week", millis_week));

            if (current_calendar2.after(nextNotifyTime_week)) {
                nextNotifyTime_week.add(Calendar.DATE, 7);
            }
            /*if (current_calendar2.after(nextNotifyTime_week)) {
                nextNotifyTime_week.add(Calendar.MINUTE, 30);
            }*/

            Date currentDateTime2 = nextNotifyTime_week.getTime();
            String date_text_week = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime2);
            Toast.makeText(context.getApplicationContext(),"[재부팅후] 다음 친구수 알람은 " + date_text_week + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();


          /*  if (manager_week != null) {
                manager_week.setRepeating(AlarmManager.RTC_WAKEUP, nextNotifyTime_week.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY*7, pendingIntent_week);
            }*/
            if (manager_week != null) {
                manager_week.setRepeating(AlarmManager.RTC_WAKEUP, nextNotifyTime_week.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY*7, pendingIntent_week);
            }
        }
    }
}
