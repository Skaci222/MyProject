package com.course.mqttapptest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class configActivity extends AppCompatActivity {

    EditText etRenameDevice1, etRenameDevice2;
    TextView tvDevice1, tvDevice2;
    Button btnRename1, btnRename2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        etRenameDevice1 = findViewById(R.id.etRenameDevice1);
        etRenameDevice2 = findViewById(R.id.etRenameDevice2);
        tvDevice1 = findViewById(R.id.tvDevice1);
        tvDevice2 = findViewById(R.id.tvDevice2);
        btnRename1 = findViewById(R.id.btnRename1);
        btnRename2 = findViewById(R.id.btnRename2);

        btnRename1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = etRenameDevice1.getText().toString();
                tvDevice1.setText(newName);
                Toast.makeText(configActivity.this, "Renamed Device", Toast.LENGTH_SHORT).show();
                //SensorA sensorA = SensorA.renameSenor(newName);
            }
        });

        btnRename2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = etRenameDevice2.getText().toString();
                tvDevice2.setText(newName);
                Toast.makeText(configActivity.this, "Renamed Device", Toast.LENGTH_SHORT).show();

            }
        });




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backIntent = new Intent(this, Dashboard.class);
        startActivity(backIntent);
    }


}