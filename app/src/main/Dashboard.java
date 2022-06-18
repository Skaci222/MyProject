package com.course.mqttapptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Dashboard extends AppCompatActivity implements RenameDialogue.RenameDialogueListener {

    public static final String TAG = "MQTT: ";

    private TextView tvTemp, tvHumidity, tvStatus1;
    private FrameLayout fragmentContainer;

    static String HOST = "tcp://192.168.1.100:1883";
    private String tempTopic, motionTopic, clientId, humidityTopic, deviceId, pubTopic;
    private MqttAndroidClient client;
    private TempFrag tempFrag;
    private NotificationManagerCompat notificationManagerCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvHumidity = findViewById(R.id.tvHumidity);
        tvTemp = findViewById(R.id.tvTemp);
        tvStatus1 = findViewById(R.id.tvStatus1);
        //fragmentContainer = findViewById(R.id.fragment_container);

        tempTopic = "temperature";
        humidityTopic = "humidity";
        motionTopic = "movement";
        clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), HOST, clientId);
        //connectX();


        ViewPager2 viewPager2 = findViewById(R.id.view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(adapter);

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

            case R.id.configure:
                Intent configIntent = new Intent(this, configActivity.class);
                startActivity(configIntent);
                return true;

            case R.id.connect:
                connectX();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
                    tvStatus1.setText("Status: Connected");
                    subscribe();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");
                }
            });
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
                    //connectX();
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
                        tvStatus1.setText("Status: Alarm has been triggered");
                        //doorAlarm();
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
    public void disconnect(){
        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Disconnected");
                    tvStatus1.setText("Status: Disconnected");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // something went wrong, but probably we are disconnected anyway
                    Log.d(TAG, "Ruh Roh");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        disconnect();
    }

    public void sendDataToFrags(){
        Bundle bundle = new Bundle();
        String message = "values";
        bundle.putString(tvTemp.toString(), message);
    }

    @Override
    public void applyName(String newName) {

    }
}