package com.course.mqttapptest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

public class TempFrag extends Fragment {
    private TextView tvTemp, tvHumidity, temp, humidity;

    public interface tempFragListener{
        void onInputSent(CharSequence input);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_temp, container, false);
        tvTemp = v.findViewById(R.id.tvTemp);
        tvHumidity = v.findViewById(R.id.tvHumidity);
        humidity = v.findViewById(R.id.textView1);
        temp = v.findViewById(R.id.textView);

        return v;
    }


}
