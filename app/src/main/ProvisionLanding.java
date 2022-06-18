package com.course.mqttapptest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.espressif.provisioning.DeviceConnectionEvent;
import com.espressif.provisioning.ESPConstants;
import com.espressif.provisioning.ESPProvisionManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class ProvisionLanding extends AppCompatActivity {

    public static final String TAG = "Provisioning: ";

    public static final int REQUEST_FINE_LOCATION = 10;
    public static final int WIFI_SETTINGS_ACTIVITY_REQUEST = 11;

    private Button btnProvision;

    private ESPProvisionManager provisionManager;
    private int securityType;
    private String deviceName, pop;
    DeviceConnectionEvent event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provision_landing);
        securityType = getIntent().getIntExtra(AppConstants.KEY_SECURITY_TYPE, 0);
        deviceName = getIntent().getStringExtra(AppConstants.KEY_DEVICE_NAME);
        pop = getIntent().getStringExtra(AppConstants.KEY_PROOF_OF_POSSESSION);

        EventBus.getDefault().register(this);
        //pop = "abcd1234";
        deviceName = "device name";
        provisionManager = ESPProvisionManager.getInstance(getApplicationContext());
        btnProvision = findViewById(R.id.btnProvision);
        btnProvision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS),
                        WIFI_SETTINGS_ACTIVITY_REQUEST);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case WIFI_SETTINGS_ACTIVITY_REQUEST:
                if(hasPermissions()) {
                    connectDevice();
                }
                break;
        }
    }

    private void connectDevice(){
        if (ActivityCompat.checkSelfPermission(ProvisionLanding.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            provisionManager.getEspDevice().connectWiFiDevice();
        } else {
            Log.e(TAG, "Not able to connect device as Location permission is not granted.");
            Toast.makeText(ProvisionLanding.this, "Please give location permission to connect device",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case REQUEST_FINE_LOCATION:
                // TODO
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeviceConnectionEvent event) {

        Log.d(TAG, "On Device Prov Event RECEIVED : " + event.getEventType());

        switch (event.getEventType()) {

            case ESPConstants.EVENT_DEVICE_CONNECTED:

                Log.e(TAG, "Device Connected Event Received");
                ArrayList<String> deviceCaps = provisionManager.getEspDevice().getDeviceCapabilities();

                if (!TextUtils.isEmpty(pop)) {

                    provisionManager.getEspDevice().setProofOfPossession(pop);

                    if (deviceCaps != null && deviceCaps.contains("wifi_scan")) {

                        goToWifiScanListActivity();

                    } else {

                        goToWifiConfigActivity();
                    }

                } else {

                    if (deviceCaps != null && !deviceCaps.contains("no_pop") && securityType == 1) {

                        goToPopActivity();

                    } else if (deviceCaps != null && deviceCaps.contains("wifi_scan")) {

                        goToWifiScanListActivity();

                    } else {

                        goToWifiConfigActivity();
                    }
                }
                break;

            case ESPConstants.EVENT_DEVICE_CONNECTION_FAILED:

                btnProvision.setText("Can't connect");
                Toast.makeText(this, "Failed to connect", Toast.LENGTH_SHORT).show();
                Log.e(TAG,"failed to connect");
                break;
        }
    }

    private void goToPopActivity() {

        finish();
        Intent popIntent = new Intent(getApplicationContext(), ProofOfPossession.class);
        startActivity(popIntent);
    }

    private void goToWifiScanListActivity(){
        finish();
        Intent wifiListIntent = new Intent(getApplicationContext(), WifiScan.class);
        startActivity(wifiListIntent);
    }

    private void goToWifiConfigActivity(){
        finish();
        Intent wifiConfigIntent = new Intent(getApplicationContext(), WiFiConfig.class);
        startActivity(wifiConfigIntent);
    }

    private boolean hasPermissions() {

        if (!hasLocationPermissions()) {

            requestLocationPermission();
            return false;
        }
        return true;
    }

    private boolean hasLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, StartScreen.class);
        startActivity(intent);
        Toast.makeText(this, "StartScreen", Toast.LENGTH_SHORT).show();
    }
}