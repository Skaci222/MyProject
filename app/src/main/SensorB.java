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
import org.w3c.dom.Text;

public class SensorB extends Fragment implements RenameDialogue.RenameDialogueListener {

    TextView sensorB;
    Button btnRename;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sensor1, container, false);
        sensorB = v.findViewById(R.id.door2);
        btnRename = v.findViewById(R.id.btnRename1);
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
        renameDialogue.setTargetFragment(SensorB.this,1);
        renameDialogue.show(getFragmentManager(), "RenameDevice");
    }

    @Override
    public void applyName(String newName) {
        sensorB.setText(newName);
    }
}
