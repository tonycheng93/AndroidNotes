package com.sky.androidnotes.service;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sky.androidnotes.R;

public class ServiceTestActivity extends AppCompatActivity {

    private static final String TAG = "bc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_test);

        final Button startService = (Button) findViewById(R.id.btn_start_service);
        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
            }
        });
    }
}
