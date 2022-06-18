package com.course.mqttapptest;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

public class ViewPagerAdapter extends FragmentStateAdapter {


    public ViewPagerAdapter(@NonNull @NotNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {

        switch(position){
            case 0:
                return new TempFrag();

            case 1:
                return new SensorA();

            default:
                return new SensorB();
        }

    }

    @Override
    public int getItemCount() {
        return 3; //number of fragments displayed
    }
}
