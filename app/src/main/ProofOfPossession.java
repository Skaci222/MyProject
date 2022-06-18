package com.course.mqttapptest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.espressif.provisioning.DeviceConnectionEvent;
import com.espressif.provisioning.ESPConstants;
import com.espressif.provisioning.ESPProvisionManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class ProofOfPossession extends AppCompatActivity {

    private static final String TAG = "POP: ";
    public static final String P_O_P = "POP";


    private Button btnNext1;
    private String deviceName;
    private TextView tvHeading;
    private EditText etPin;
    private ESPProvisionManager provisionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proof_of_possession);

        tvHeading = findViewById(R.id.tvHeading);
        etPin = findViewById(R.id.etPin);
        //final String pop = etPin.getText().toString();
        final String pop = "abcd1234";
        btnNext1 = findViewById(R.id.btnNext1);
        btnNext1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "POP : " + pop);
                provisionManager.getEspDevice().setProofOfPossession(pop);
                ArrayList<String> deviceCaps = provisionManager.getEspDevice().getDeviceCapabilities();

                //if (deviceCaps.contains("wifi_scan")) {
                    //goToWiFiScanListActivity();
                //} else {
                    goToWiFiConfigActivity();
                //}
            }
        });

        provisionManager = ESPProvisionManager.getInstance(getApplicationContext());
        EventBus.getDefault().register(this);

        deviceName = provisionManager.getEspDevice().getDeviceName();

        /*if (!TextUtils.isEmpty(deviceName)) {
            String popText = getString(R.string.pop_instruction) + " " + deviceName;
            tvHeading.setText(popText);
        }*/

        //String pop = "abcd1234";

        if (!TextUtils.isEmpty(pop)) {

            etPin.setText(pop);
            etPin.setSelection(etPin.getText().length());
        }
        etPin.requestFocus();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        provisionManager.getEspDevice().disconnectDevice();
        super.onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeviceConnectionEvent event) {

        Log.d(TAG, "On Device Connection Event RECEIVED : " + event.getEventType());

        switch (event.getEventType()) {

            case ESPConstants.EVENT_DEVICE_DISCONNECTED:
                if (!isFinishing()) {
                    //showAlertForDeviceDisconnected();
                }
                break;
        }
    }

    private View.OnClickListener cancelBtnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            provisionManager.getEspDevice().disconnectDevice();
            finish();
        }
    };

    private void goToWiFiScanListActivity() {

        Intent wifiListIntent = new Intent(getApplicationContext(), WifiScan.class);
        wifiListIntent.putExtras(getIntent());
        startActivity(wifiListIntent);
        finish();
    }

    private void goToWiFiConfigActivity() {
        String pop = "abcd1234";
        Intent wifiConfigIntent = new Intent(getApplicationContext(), WiFiConfig.class);
        wifiConfigIntent.putExtras(getIntent());
        wifiConfigIntent.putExtra(P_O_P, pop);
        startActivity(wifiConfigIntent);
        finish();
    }

    /*private void showAlertForDeviceDisconnected() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.error_title);
        builder.setMessage(R.string.dialog_msg_ble_device_disconnection);

        // Set up the buttons
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.show();
    }*/
}