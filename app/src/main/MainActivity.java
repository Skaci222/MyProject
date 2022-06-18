package com.course.mqttapptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    Button btnConnect, btnPublish, btnSub, btnDisconnect;
    EditText etPublish, etTopic;
    TextView tvTemp, tvStatus, tvHumidity;


    static String HOST = "tcp://192.168.1.100:1883";
    private String tempTopic, motionTopic, clientId, humidityTopic, deviceId, pubTopic;
    private MqttAndroidClient client;
    private NotificationManagerCompat notificationManagerCompat;

    public static final String TAG = "MQTT: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConnect = findViewById(R.id.btnConnect);
        btnPublish = findViewById(R.id.btnPublish);
        btnSub = findViewById(R.id.btnSub);
        btnDisconnect = findViewById(R.id.btnDisconnect);
        etPublish = findViewById(R.id.etPublish);
        tvTemp = findViewById(R.id.tvTemp);
        tvHumidity = findViewById(R.id.tvHumidity);
        etTopic = findViewById(R.id.etTopic);
        tvStatus = findViewById(R.id.tvStatus);
        //clientId = "a:y94ieb:andId-001";
        //deviceId = "d:y94ieb:Android:and-001";
        //topic = "iot-2/evt/event/fmt/string";
        //pubTopic = "iot-2/evt/temperature/fmt/JSON";
        //topic = "iot-2/cmd/temperature/fmt/JSON";
        tempTopic = "temperature";
        humidityTopic = "humidity";
        motionTopic = "movement";
        clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), HOST, clientId);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectX();
            }
        });
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publish();
            }
        });
        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscribe();
            }
        });
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        notificationManagerCompat = NotificationManagerCompat.from(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    //@Override
    //public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //switch(item.getItemId()){

            //case R.id.device1:
                //Intent espMain = new Intent(this, ProvisionLanding.class);
                //startActivity(espMain);
                //Toast.makeText(this, "Provision starting", Toast.LENGTH_SHORT).show();
                //return true;
            //case R.id.device2:
               // Intent espMain2 = new Intent(this, ProvisionLanding.class);
                //startActivity(espMain2);
                //Toast.makeText(this, "DEVICE 2", Toast.LENGTH_SHORT).show();
                //return true;
            //case R.id.connect:
                //connectX();
                //return true;
        //}
        //return super.onOptionsItemSelected(item);
    //}

    private void connectX() {

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("pladi");//a-y94ieb-jwu3dqa39k
        options.setPassword("gjakova".toCharArray());//Ox6ipL6xIYw@SzT9xn
        options.setKeepAliveInterval(1000000);

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "Connected successfully");
                    Toast.makeText(MainActivity.this, "Connected!", Toast.LENGTH_SHORT).show();
                    btnConnect.setVisibility(View.INVISIBLE);
                    btnDisconnect.setVisibility(View.VISIBLE);
                    tvStatus.setText("Status: Connected, armed");
                    subscribe();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");
                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(){
        String myTopic = etTopic.getText().toString();
        String message = etPublish.getText().toString();
        //byte[] encodedPayload = new byte[0];
        try {
            //encodedPayload = payload.getBytes("UTF-8");
            //MqttMessage message = new MqttMessage(encodedPayload);
            //message.setRetained(true);
            client.publish(myTopic, message.getBytes(), 0, false);
            etTopic.setText("");
            etPublish.setText("");
            Log.d(TAG, "Published " + "'" + message + "'" + " to " + myTopic + " topic");
            Toast.makeText(this, "Message Sent!", Toast.LENGTH_SHORT).show();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(){
        try{
            client.subscribe(tempTopic, 0);
            client.subscribe(motionTopic, 0);
            client.subscribe(humidityTopic,0);
            Log.d(TAG, "subscribed to " + tempTopic);
            Log.d(TAG, "subscribed to " + motionTopic);
            Log.d(TAG, "subscribed to " + humidityTopic);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d(TAG, "connection has been lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    if(topic.equals(tempTopic)) {
                        tvTemp.setText(message.toString());
                    }
                    else if(topic.equals(humidityTopic)){
                        tvHumidity.setText(message.toString());
                    }
                    if(topic.equals(motionTopic) && message.toString().equals("100")){
                        tvStatus.setText("Status: Alarm has been triggered");
                        doorAlarm();
                    }
                    Log.d(TAG, "message: " + new String(message.getPayload()));

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d(TAG, "delivery has been completed!");
                }
            });
        }catch(MqttException e){

        }
    }

    public void unSubscribe(){
        final String topic = "testTopic";
        try {
            IMqttToken unsubToken = client.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Unsubscribed from " + topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.d(TAG, "whoops, can't unsub");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(){
        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Disconnected");
                    tvStatus.setText("Status: Disconnected");
                    btnConnect.setVisibility(View.VISIBLE);
                    btnDisconnect.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // something went wrong, but probably we are disconnected anyway
                    Log.d(TAG, "Ruh, Roh");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void doorAlarm(){
        Notification notification = new NotificationCompat.Builder(this,
                App.CHANNEL_1_ID).setSmallIcon(R.drawable.ic_security)
                .setContentTitle("Door alarm has been triggered")
                .setContentText("THERE'S SOMEONE IN YOUR HOUSE GET OUUUUT")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .build();
        notificationManagerCompat.notify(1, notification);
    }

}