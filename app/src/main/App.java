package com.course.mqttapptest;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_1_ID = "door_alarm";
    public static final String CHANNEL_2_ID= "temp_notification";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel
                    (CHANNEL_1_ID, "Door Alarm", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("This gets triggered when unusual movement is detected by a device");
            NotificationChannel channel2 = new NotificationChannel
                    (CHANNEL_2_ID, "Is it too hot?", NotificationManager.IMPORTANCE_LOW);
            channel2.setDescription("This gets triggered when the temperature goes above the indicated level");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }
}
