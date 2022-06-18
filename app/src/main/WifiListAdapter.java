package com.course.mqttapptest;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.espressif.provisioning.WiFiAccessPoint;

import java.util.ArrayList;

public class WifiListAdapter extends ArrayAdapter<WiFiAccessPoint> {
    private Context context;
    private ArrayList<WiFiAccessPoint> wifiApList;

    public WifiListAdapter(Context context, int resource, ArrayList<WiFiAccessPoint> wifiApList) {
        super(context, resource);
        this.context = context;
        this.wifiApList = wifiApList;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        WiFiAccessPoint wiFiAccessPoint = wifiApList.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.wifi_access_point, null);

        TextView tvWifiName = view.findViewById(R.id.tvWifiName);
        tvWifiName.setText(wiFiAccessPoint.getWifiName());

        return view;
    }
}
