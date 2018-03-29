package com.emcsthai.bluetooth.sppr210.testbluetoothspp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnBluetoothSPP;
    private Button btnOldSDKv236;
    private Button btnNewBXLSDKv127;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidget();

        btnBluetoothSPP.setOnClickListener(onClickListener);
        btnOldSDKv236.setOnClickListener(onClickListener);
        btnNewBXLSDKv127.setOnClickListener(onClickListener);
    }

    private void initWidget() {
        btnBluetoothSPP = findViewById(R.id.btnBluetoothSPP);
        btnOldSDKv236 = findViewById(R.id.btnOldSDKv236);
        btnNewBXLSDKv127 = findViewById(R.id.btnNewBXLSDKv127);
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v == btnBluetoothSPP) {
                Intent intent = new Intent(v.getContext(), BluetoothSPPActivity.class);
                startActivity(intent);
            }

            if (v == btnOldSDKv236) {
                Intent intent = new Intent(v.getContext(), OldSDKv236Activity.class);
                startActivity(intent);
            }

            if (v == btnNewBXLSDKv127) {
                Intent intent = new Intent(v.getContext(), NewBXLSDKv127Activity.class);
                startActivity(intent);
            }
        }
    };
}
