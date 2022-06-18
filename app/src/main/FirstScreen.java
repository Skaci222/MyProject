package com.course.mqttapptest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.espressif.provisioning.ESPDevice;
import com.espressif.provisioning.ESPProvisionManager;
import com.espressif.provisioning.WiFiAccessPoint;
import com.espressif.provisioning.listeners.ProvisionListener;

import java.util.List;

public class FirstScreen extends AppCompatActivity {

    public static final String TAG = "FirstScreen: ";

    private TextView tv1;
    private ESPProvisionManager manager;
    private ProvisionListener listener;
    private ESPDevice device;
    private SharedPreferences sharedPreferences;
    private WifiInfo wifiInfo;
    private String deviceType;
    private Handler handler = new Handler();
    private Context context;
    private WifiManager wifiManager;
    private List<ScanResult> results;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        sharedPreferences = getSharedPreferences(AppConstants.ESP_PREFERENCES, Context.MODE_PRIVATE);
        manager = ESPProvisionManager.getInstance(getApplicationContext());
        tv1 = findViewById(R.id.tv1);
        animate();
        //scanWifi();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setScreen();
            }
        }, 4000);

    }

    public void setScreen(){

        if(manager.getEspDevice() == null){
            Log.d(TAG, "no device found, going to config screen");
            Intent intent = new Intent(this, StartScreen.class);
            startActivity(intent);
        } else {
            Log.d(TAG, "device found, going to dashboard");
            Intent intent1 = new Intent(this, MainActivity.class);
            startActivity(intent1);
        }

    }


    public void scanWifi(){
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    Log.d(TAG, "scanSuccess");
                    scanSuccess();
                } else {
                    Log.d(TAG, "scanFailure");
                    scanFailure();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);

        boolean success = wifiManager.startScan();
        if (!success) {
            // scan failure handling
            Log.d(TAG, "scanning failed");
        }
    }


    private void scanSuccess() {
        List<ScanResult> results = wifiManager.getScanResults();
    }

    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        List<ScanResult> results = wifiManager.getScanResults();
    }

    public void animate() {
        YoYo.with(Techniques.FadeOut)
                .duration(3000)
                .repeat(3)
                .playOn(tv1);
    }
}
