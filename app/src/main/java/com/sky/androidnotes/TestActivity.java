package com.sky.androidnotes;

import android.app.Activity;
import android.content.Context;
import android.icu.text.LocaleDisplayNames;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TestActivity extends Activity {

    private static final String TAG = "TestActivity";

//    private ErrorView errorView;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        errorView = new ErrorView(this);
//        setContentView(errorView);

        setContentView(R.layout.activity_for_test);

        final List<User> userList = new ArrayList<>();

        final List<User> users = new ArrayList<>();

        User user1 = new User("tony1", "123");
//        list.add(user1);
        userList.add(user1);
        User user2 = new User("tony2", "123");
//        list.add(user2);
        userList.add(user2);
        User user3 = new User("tony3", "123");
//        list.add(user3);
        userList.add(user3);

        final Button button = (Button) findViewById(R.id.btn_test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + userList.containsAll(users));
//                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//                if (wifiManager != null) {
//                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//                    final String ssid = wifiInfo.getSSID();
//                    button.setText(ssid);
//                }
                final boolean ping = ping();
                Log.d(TAG, "onClick: ping = " + ping);
            }
        });
    }

    private boolean ping() {
        String result = null;
        String ip = "www.baidu.com";
        try {
            Process process = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);
            final InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String content = "";
            while ((content = reader.readLine()) != null) {
                stringBuilder.append(content);
            }
            Log.d(TAG, "ping: content = " + stringBuilder.toString());
            int status = process.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = "IOException";
        } catch (InterruptedException e) {
            e.printStackTrace();
            result = "InterruptedException";
        }
        return false;
    }

    private class User {

        private String name;
        private String password;

        public User(String name, String password) {
            this.name = name;
            this.password = password;
        }
    }
}
