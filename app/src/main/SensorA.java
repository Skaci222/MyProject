package com.course.mqttapptest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

public class SensorA extends Fragment implements RenameDialogue.RenameDialogueListener {

    TextView sensorA;
    Button btnRename;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sensor, container, false);
        sensorA = v.findViewById(R.id.door1);
        btnRename = v.findViewById(R.id.btnRename);
        btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogue();
            }
        });


        return v;
    }

    public void openDialogue(){
        RenameDialogue renameDialogue = new RenameDialogue();
        renameDialogue.setTargetFragment(SensorA.this,1);
        renameDialogue.show(getFragmentManager(), "RenameDevice");
    }

    @Override
    public void applyName(String newName) {
        sensorA.setText(newName);
    }
}
