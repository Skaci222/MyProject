package com.course.mqttapptest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.espressif.provisioning.DeviceConnectionEvent;
import com.espressif.provisioning.ESPConstants;
import com.espressif.provisioning.ESPProvisionManager;
import com.espressif.provisioning.WiFiAccessPoint;
import com.espressif.provisioning.listeners.WiFiScanListener;
import com.espressif.provisioning.transport.Transport;
import com.google.android.material.textfield.TextInputLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class WifiScan extends AppCompatActivity {

    public static final String TAG = "WifiScanActivity: ";
    public static final String KEY_WIFI_SSID = "ssid";
    public static final String KEY_WIFI_PASSWORD = "password";

    private Handler handler;
    private ListView wifiListView;
    private WifiListAdapter adapter;
    private ArrayList<WiFiAccessPoint> wifiApList;
    private ESPProvisionManager provisionManager;
    private Transport transport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_scan);
        wifiListView = findViewById(R.id.wifi_ap_list);
        wifiApList = new ArrayList<>();
        handler = new Handler();
        provisionManager = ESPProvisionManager.getInstance(getApplicationContext());

        String deviceName = provisionManager.getEspDevice().getDeviceName();
        adapter = new WifiListAdapter(this, R.id.tvWifiName, wifiApList);

        //Assign adapter to ListView
        wifiListView.setAdapter(adapter);
        wifiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Device to be connected: " + wifiApList.get(position));
                String ssid = wifiApList.get(position).getWifiName();

                if(ssid.equals("Join Other Network")){
                    askForNetwork(wifiApList.get(position).getWifiName(), wifiApList.get(position).getSecurity());
                } else if (wifiApList.get(position).getSecurity() == ESPConstants.WIFI_OPEN){
                    goForProvisioning(wifiApList.get(position).getWifiName(), "");
                } else {
                    askForNetwork(wifiApList.get(position).getWifiName(), wifiApList.get(position).getSecurity());
                }
            }
        });
        EventBus.getDefault().register(this);
        startWifiScan();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeviceConnectionEvent event) {

        Log.d(TAG, "On Device Connection Event RECEIVED : " + event.getEventType());

        switch (event.getEventType()) {

            case ESPConstants.EVENT_DEVICE_DISCONNECTED:
                if (!isFinishing()) {
                    showAlertForDeviceDisconnected();
                    Log.d(TAG, "device disconnected");
                    Toast.makeText(this, "device disconnected", Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }

    private void startWifiScan(){

        Log.d(TAG, "Start Wi-Fi scan");
        wifiApList.clear();

        provisionManager.getEspDevice().scanNetworks(new WiFiScanListener() {
            @Override
            public void onWifiListReceived(ArrayList<WiFiAccessPoint> wifiList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wifiApList.addAll(wifiList);
                        completeWifiList();
                    }
                });
            }

            @Override
            public void onWiFiScanFailed(Exception e) {
                Log.e(TAG, "onWiFiScanFailed");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WifiScan.this, "Failed to get Wi-Fi scan list", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

    }
    private void completeWifiList(){

        //TODO
    }
    private void askForNetwork(final String ssid, final int authMode){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_wifi_network, null);
        builder.setView(dialogView);

        final EditText etSsid = findViewById(R.id.etSsid);
        final EditText etPassword = findViewById(R.id.etPassword);

        if(ssid.equals("Join Other Network")){
            builder.setTitle("Enter Network Information");
        } else {
            builder.setTitle(ssid);
            etSsid.setVisibility(View.GONE);
        }

        builder.setPositiveButton("Provision", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    String password = etPassword.getText().toString();

                    if(ssid.equals("Join Other Network")){
                        String networkName = etSsid.getText().toString();

                        if(TextUtils.isEmpty(networkName)){
                            etSsid.setError("Network name cannot be empty");
                        } else {
                            dialog.dismiss();
                            goForProvisioning(networkName, password);
                        }

                    } else {
                        if(TextUtils.isEmpty(password)) {

                            if (authMode != ESPConstants.WIFI_OPEN) {
                                etPassword.setError("Password cannot be empty");
                            } else {
                                dialog.dismiss();
                                goForProvisioning(ssid, password);
                            }

                        } else {
                                if(authMode == ESPConstants.WIFI_OPEN){
                                    password = "";
                                }
                                dialog.dismiss();
                                goForProvisioning(ssid, password);
                            }
                    }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void goForProvisioning(String ssid, String password){
        finish();
        Intent provisionIntent = new Intent(getApplicationContext(), ProvisionActivity.class);
        provisionIntent.putExtras(getIntent());
        provisionIntent.putExtra(KEY_WIFI_SSID, ssid);
        provisionIntent.putExtra(KEY_WIFI_PASSWORD, password);
        startActivity(provisionIntent);
    }
    private void showAlertForDeviceDisconnected(){

    }
}

