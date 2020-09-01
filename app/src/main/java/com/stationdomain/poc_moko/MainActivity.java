package com.stationdomain.poc_moko;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.moko.support.MokoSupport;
import com.moko.support.callback.MokoScanDeviceCallback;
import com.moko.support.entity.DeviceInfo;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MokoScanDeviceCallback {

    Button btnStart, btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);
        btnStop = findViewById(R.id.btnStop);
        btnStop.setOnClickListener(this);

        MokoSupport.getInstance().init(getApplicationContext());


    }

    @Override
    public void onClick(View view) {

    }

    public void onclickStop(View view) {
        MokoSupport.getInstance().stopScanDevice();

    }

    public void onclickStart(View view) {
        MokoSupport.getInstance().startScanDevice();
    }

    @Override
    public void onStartScan() {

    }

    @Override
    public void onScanDevice(DeviceInfo device) {

    }

    @Override
    public void onStopScan() {

    }
}