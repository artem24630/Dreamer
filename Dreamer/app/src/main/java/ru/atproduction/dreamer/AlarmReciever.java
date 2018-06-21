package ru.atproduction.dreamer;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.AlarmManager.INTERVAL_DAY;
import static android.app.AlarmManager.INTERVAL_FIFTEEN_MINUTES;
import static android.app.AlarmManager.INTERVAL_HOUR;
import static android.app.AlarmManager.INTERVAL_HALF_HOUR;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by at020 on 15.02.2018.
 */

public class AlarmReciever extends BroadcastReceiver {
    SharedPreferences sPref;
    long time1;
    long time2;
    Calendar tm1;
    Calendar tm2;



    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl= pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"YOUR TAG");

        wl.acquire();

        if(isKodTrue(context,2)){
            loadTm(context);
            Calendar stm = Calendar.getInstance();
            stm.setTimeInMillis(System.currentTimeMillis());
            if((stm.get(Calendar.HOUR)<tm1.get(Calendar.HOUR))&&(stm.get(Calendar.MINUTE)<tm1.get(Calendar.MINUTE))&&(stm.get(Calendar.HOUR)>tm2.get(Calendar.HOUR))&&(stm.get(Calendar.MINUTE)>tm2.get(Calendar.MINUTE)))
                createNotif(context,intent);

        }
        else
            createNotif(context,intent);


        Bundle ext=intent.getExtras();
        SetAlarm(context,ext.getInt("time"),ext.getString("name"),ext.getInt("id"));
        wl.release();
    }

    void createNotif(Context context,Intent intent){
        Bundle extras= intent.getExtras();

        int idn = extras.getInt("id");

        String txt = intent.getStringExtra("name");

        Notification notification = new  Notification.Builder(context).setSmallIcon(R.drawable.ic_action_cloud).setContentTitle("Dream").setContentText(txt).build();
        notification.defaults = Notification.DEFAULT_ALL;

        if(isKodTrue(context,1)) {
            notification.flags = notification.flags | Notification.FLAG_INSISTENT;
        }
        else {

            notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(idn);
        notificationManager.notify(idn, notification);

    }

    public void SetAlarm(Context context, int a, String name, int id) {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);


        Intent intent=new Intent(context, AlarmReciever.class);
        intent.putExtra("time",a);



        intent.putExtra("id",id);
        intent.putExtra("name", name);//Задаем параметр интента
        PendingIntent pi= PendingIntent.getBroadcast(context,id, intent,0);



        switch (a)
        {
            case 0:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+900000,pi);
                    Toast.makeText(context, "Alarm ", Toast.LENGTH_SHORT).show();
                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    long alarmTime = System.currentTimeMillis()+900000;
                    AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(alarmTime,pi);
                    am.setAlarmClock(info,pi);
                }


//                    am.setExact(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+900000,pi);



                break;
            case 1:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+INTERVAL_HALF_HOUR,pi);
                }
                else  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    long alarmTime = System.currentTimeMillis() +INTERVAL_HALF_HOUR;
                    AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(alarmTime, pi);
                    am.setAlarmClock(info, pi);
                }
                break;
            case 2:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+3600000,pi);
                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                long alarmTime = System.currentTimeMillis() +INTERVAL_HOUR;
                AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(alarmTime, pi);
                am.setAlarmClock(info, pi);
            }
            break;
            case 3:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+14400000,pi);
                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    long alarmTime = System.currentTimeMillis() + 4*INTERVAL_HOUR;
                    AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(alarmTime, pi);
                    am.setAlarmClock(info, pi);
                }
                break;
            case 4:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+259200000,pi);
                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    long alartTime = System.currentTimeMillis()+INTERVAL_DAY*3;
                    AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(alartTime,pi);
                    am.setAlarmClock(info,pi);
                }
                break;


        }

    }

    public void CancelAlarm(Context context,int id) {
        Intent intent=new Intent(context, AlarmReciever.class);
        PendingIntent sender= PendingIntent.getBroadcast(context,id, intent,0);
        AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);//Отменяем будильник, связанный с интентом данного класса
    }

    boolean isKodTrue(Context context,int i){
    sPref = context.getSharedPreferences("MyPref",MODE_PRIVATE);

        return sPref.getInt("kod"+i,0)==1;

    }
    void loadTm(Context context){
        sPref = context.getSharedPreferences("MyPref",MODE_PRIVATE);
        tm1=Calendar.getInstance();
        tm2 = Calendar.getInstance();
        tm1.setTimeInMillis(sPref.getLong("dateAndTime1",0));
        tm2.setTimeInMillis(sPref.getLong("dateAndTime2",0));
        time1 = sPref.getLong("dateAndTime1",0);
        time2 = sPref.getLong("dateAndTime2",0);
    }


}
