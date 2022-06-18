package com.course.mqttapptest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.espressif.provisioning.ESPConstants;
import com.espressif.provisioning.ESPProvisionManager;

public class StartScreen extends AppCompatActivity {

    public static final String TAG = "StartScreen: ";
    private static final int REQUEST_LOCATION = 1;



    private TextView tvWelcome, tvInit;
    private Button btnConfig;
    private ImageView ivSecurity;
    private ESPProvisionManager provisionManager;
    private SharedPreferences sharedPreferences;
    private String deviceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        sharedPreferences = getSharedPreferences(AppConstants.ESP_PREFERENCES, Context.MODE_PRIVATE);
        provisionManager = ESPProvisionManager.getInstance(getApplicationContext());

        tvWelcome = findViewById(R.id.tvWelcome);
        tvInit= findViewById(R.id.tvInit);
        ivSecurity = findViewById(R.id.ivSecurity);
        btnConfig = findViewById(R.id.btnConfig);
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProvisioningFlow();
            }
        });
        if (!isLocationEnabled()) {
            askForLocation();
            return;
        }

    }

    /*public void goToConfigDevices(){
        Intent intent = new Intent(this, EspMainActivity.class);
        startActivity(intent);
    }*/
    private void startProvisioningFlow(){
        deviceType = sharedPreferences.getString(AppConstants.KEY_DEVICE_TYPES, AppConstants.DEVICE_TYPE_DEFAULT);
        final boolean isSec1 = sharedPreferences.getBoolean(AppConstants.KEY_SECURITY_TYPE, true);
        Log.d(TAG, "Device Types : " + deviceType);
        Log.d(TAG, "isSec1 : " + isSec1);
        int securityType = 0;
        if (isSec1) {
            securityType = 1;
        }

        if(deviceType.equals(AppConstants.DEVICE_TYPE_BOTH) || deviceType.equals(AppConstants.DEVICE_TYPE_SOFTAP)){
            if(isSec1){
                provisionManager.createESPDevice(ESPConstants.TransportType.TRANSPORT_SOFTAP,
                        ESPConstants.SecurityType.SECURITY_1);
            } else{
                provisionManager.createESPDevice(ESPConstants.TransportType.TRANSPORT_SOFTAP,
                        ESPConstants.SecurityType.SECURITY_0);
            }
            goToWiFiProvisionLandingActivity(securityType);
        }
    }
    private void askForLocation() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage("Please enable location services to continue");

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_LOCATION);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private boolean isLocationEnabled() {

        boolean gps_enabled = false;
        boolean network_enabled = false;
        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Activity.LOCATION_SERVICE);

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        Log.d(TAG, "GPS Enabled : " + gps_enabled + " , Network Enabled : " + network_enabled);

        boolean result = gps_enabled || network_enabled;
        return result;
    }

    private void goToWiFiProvisionLandingActivity(int securityType) {

        Intent intent = new Intent(StartScreen.this, ProvisionLanding.class);
        intent.putExtra(AppConstants.KEY_SECURITY_TYPE, securityType);
        startActivity(intent);
    }
}