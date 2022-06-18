package com.course.mqttapptest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.espressif.provisioning.DeviceConnectionEvent;
import com.espressif.provisioning.ESPConstants;
import com.espressif.provisioning.ESPDevice;
import com.espressif.provisioning.ESPProvisionManager;
import com.espressif.provisioning.listeners.ProvisionListener;
import com.espressif.provisioning.listeners.ResponseListener;
import com.espressif.provisioning.security.Security;
import com.espressif.provisioning.security.Security0;
import com.espressif.provisioning.security.Security1;
import com.espressif.provisioning.transport.SoftAPTransport;
import com.espressif.provisioning.transport.Transport;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import espressif.WifiConfig;

public class ProvisionActivity extends AppCompatActivity {

    public static final String TAG = "Provisioning: ";
    public static final String KEY_WIFI_SSID = "ssid";
    public static final String KEY_WIFI_PASSWORD = "password";
    public static final String P_O_P = "POP";
    public static final String CUSTOM_ENDPOINT = "custom123";

    private TextView tvSendingWifi, tvApplyingConnection, tvCheckStatus, tvSuccess;
    private Button btnHome;
    private ImageView ivProv;

    private String ssidValue, passphraseValue = "";
    private ESPProvisionManager provisionManager;
    private boolean isProvisioningCompleted = false;
    private Transport transport;
    private Security1 security;
    private ESPDevice device;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provision);

        tvSendingWifi = findViewById(R.id.tvSendingWifi);
        tvApplyingConnection = findViewById(R.id.tvApplyingConnection);
        tvCheckStatus = findViewById(R.id.tvCheckStatus);
        tvSuccess = findViewById(R.id.tvSuccess);

        Intent intent = getIntent();
        ssidValue = intent.getStringExtra(KEY_WIFI_SSID);
        passphraseValue = intent.getStringExtra(KEY_WIFI_PASSWORD);
        provisionManager = ESPProvisionManager.getInstance(getApplicationContext());
        transport = new SoftAPTransport();
        final String pop = intent.getStringExtra(P_O_P);
        security = new Security1(pop);

        EventBus.getDefault().register(this);
        Log.d(TAG, "Selected AP " + ssidValue);
        device = provisionManager.getEspDevice();

        sendToEndpoint();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        doProvisioning();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeviceConnectionEvent event) {

        Log.d(TAG, "On Device Connection Event RECEIVED : " + event.getEventType());

        switch (event.getEventType()) {

            case ESPConstants.EVENT_DEVICE_DISCONNECTED:
                if (!isFinishing() && !isProvisioningCompleted) {
                    //showAlertForDeviceDisconnected();
                    Log.d(TAG, "device disconnected");
                    Toast.makeText(this, "device disconnected", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void sendToEndpoint(){

        JSONObject obj = new JSONObject();
        try {
            obj.put("URI", "tcp://192.168.1.100:1883");
            obj.put("Port", "1883");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[]b = obj.toString().getBytes();
        device.sendDataToCustomEndPoint(CUSTOM_ENDPOINT, b, new ResponseListener() {
            @Override
            public void onSuccess(byte[] returnData) {
                Log.d("SendCustomMsg", "return data: " + returnData);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("SendCustomMsg", "Failed to Send");
            }
        });

    }


    private void doProvisioning(){

        provisionManager.getEspDevice().provision(ssidValue, passphraseValue, new ProvisionListener() {

            @Override
            public void createSessionFailed(Exception e) {
                Log.e(TAG, "Failed to create session");
            }

            @Override
            public void wifiConfigSent() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Wifi config sent");

                    }
                });
            }

            @Override
            public void wifiConfigFailed(Exception e) {
                Log.e(TAG, "Wifi config failed");
            }

            @Override
            public void wifiConfigApplied() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Wifi config applied");


                    }
                });
            }

            @Override
            public void wifiConfigApplyFailed(Exception e) {
                Log.e(TAG, "Failed to apply wifi config");
            }

            @Override
            public void provisioningFailedFromDevice(ESPConstants.ProvisionFailureReason failureReason) {

                switch(failureReason){
                    case AUTH_FAILED:
                        Log.e(TAG, "authentication failed");
                        break;
                    case NETWORK_NOT_FOUND:
                        Log.e(TAG, "network not found");
                        break;
                    case DEVICE_DISCONNECTED:
                        Log.e(TAG, "device disconnected");
                    case UNKNOWN:
                        Log.e(TAG, "failed to provision device");
                }
            }

            @Override
            public void deviceProvisioningSuccess() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isProvisioningCompleted = true;
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onProvisioningFailed(Exception e) {
                Log.e(TAG, "provisioning failed");
            }
        });
    }
}