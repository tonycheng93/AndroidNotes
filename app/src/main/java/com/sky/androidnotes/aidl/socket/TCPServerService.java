package com.sky.androidnotes.aidl.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author tonycheng
 */
public class TCPServerService extends Service {

    private static final String TAG = "TCPServerService";

    private boolean mIsServiceDestroyed = false;
    private String[] mRandomMessage = new String[]{
            "Hello client!",
            "来自Server的问候",
            "我来自Server",
            "Server欢迎你"
    };

    public TCPServerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        getSinglePoolExecutor().execute(new TCPRunnale());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroyed = true;
        super.onDestroy();
    }

    private class TCPRunnale implements Runnable {

        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(8899);
            } catch (IOException e) {
                Log.d(TAG, "run: establish connection failed,port 8688");
                e.printStackTrace();
                return;
            }
            while (!mIsServiceDestroyed) {
                try {
                    final Socket client = serverSocket.accept();
                    Log.d(TAG, "run: client = " + client + " has established connection.");
                    getSinglePoolExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            responseClient(client);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void responseClient(Socket client) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(client.getOutputStream())), true);
                out.println("欢迎来到聊天室");
                while (!mIsServiceDestroyed) {
                    final String msg = reader.readLine();
                    Log.d(TAG, "responseClient: message form client = " + msg);
                    if (msg == null) {
                        //断开连接
                        break;
                    }
                    final int i = new Random().nextInt(mRandomMessage.length);
                    String responseMsg = mRandomMessage[i];
                    out.println(responseMsg);
                    Log.d(TAG, "responseClient: send message: " + responseMsg);
                }
                Log.d(TAG, "responseClient: client quit.");
                out.close();
                reader.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private ExecutorService getSinglePoolExecutor() {
        ExecutorService singlePoolExecutor = null;
        ThreadFactory namedThreadFactory = Executors.defaultThreadFactory();
        singlePoolExecutor = new ThreadPoolExecutor(1, 1, 0L,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1024),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy());
        return singlePoolExecutor;
    }
}
