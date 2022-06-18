package com.course.mqttapptest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.espressif.provisioning.DeviceConnectionEvent;
import com.espressif.provisioning.ESPConstants;
import com.espressif.provisioning.ESPDevice;
import com.espressif.provisioning.ESPProvisionManager;
import com.espressif.provisioning.listeners.ResponseListener;
import com.espressif.provisioning.security.Security;
import com.espressif.provisioning.transport.Transport;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class WiFiConfig extends AppCompatActivity {

    public static final String TAG = "WifiConfig: ";
    public static final String KEY_WIFI_SSID = "ssid";
    public static final String KEY_WIFI_PASSWORD = "password";
    public static final String CUSTOM_ENDPOINT = "custom-data";
    public static final String  P_O_P = "POP";

    private Button btnNext;
    private EditText etSsidInput, etPasswordInput;
    private ESPProvisionManager provisionManager;
    private ESPDevice device;
    private String proofOP;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_config);

        EventBus.getDefault().register(this);
        provisionManager = ESPProvisionManager.getInstance(getApplicationContext());
        etSsidInput = findViewById(R.id.etSsidInput);
        etPasswordInput = findViewById(R.id.etPasswordInput);
        intent = getIntent();
        proofOP = intent.getStringExtra(P_O_P);
        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ssid = etSsidInput.getText().toString();
                String password = etPasswordInput.getText().toString();

                if(TextUtils.isEmpty(ssid)){
                    etSsidInput.setError("Network name cannot be empty");
                }
                goToProvisionActivity(ssid, password, proofOP);
            }
        });
        device = provisionManager.getEspDevice();
        //sendToEndpoint();
    }

    /*public void sendToEndpoint(){
        String s = "herro";
        byte[] data = s.getBytes();
        device.sendDataToCustomEndPoint(CUSTOM_ENDPOINT, data, new ResponseListener() {
            @Override
            public void onSuccess(byte[] returnData) {
                Log.d(TAG, "return data: " + returnData.toString());
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("Sending Data", "Failed");
            }
        });
    }*/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeviceConnectionEvent event) {

        Log.d(TAG, "On Device Connection Event RECEIVED : " + event.getEventType());

        switch (event.getEventType()) {

            case ESPConstants.EVENT_DEVICE_DISCONNECTED:
                if (!isFinishing()) {
                    //showAlertForDeviceDisconnected();
                    Log.d(TAG, "device disconnected");
                    Toast.makeText(this, "device disconnected", Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }


    private void goToProvisionActivity(String ssid, String password, String pop){
        finish();
        Intent provisionIntent = new Intent(getApplicationContext(), ProvisionActivity.class);
        provisionIntent.putExtras(getIntent());
        provisionIntent.putExtra(KEY_WIFI_SSID,ssid);
        provisionIntent.putExtra(KEY_WIFI_PASSWORD, password);
        provisionIntent.putExtra(P_O_P, pop);
        startActivity(provisionIntent);
    }
}