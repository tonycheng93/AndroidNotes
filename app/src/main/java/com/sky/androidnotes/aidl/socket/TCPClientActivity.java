package com.sky.androidnotes.aidl.socket;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sky.androidnotes.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author tonycheng
 */
public class TCPClientActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TCPClientActivity";

    private TextView mTvMessageView;
    private EditText mEtInputView;
    private Button mBtnSendView;

    private static final int MESSAGE_RECEIVED_NEW_MSG = 0;
    private static final int MESSAGE_SOCKET_CONNECTED = 1;

    private PrintWriter mPrintWriter;
    private Socket mClientSocket;
    private UIHandler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcpclient);

        mTvMessageView = (TextView) findViewById(R.id.tv_message);
        mEtInputView = (EditText) findViewById(R.id.et_input);
        mBtnSendView = (Button) findViewById(R.id.btn_send);
        mBtnSendView.setOnClickListener(this);

        uiHandler = new UIHandler(this, getMainLooper());

        Intent intent = new Intent(TCPClientActivity.this, TCPServerService.class);
        startService(intent);

        getSingleThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                connectTCPServer();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mClientSocket != null) {
            try {
                mClientSocket.shutdownInput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    private void connectTCPServer() {
        Socket socket = null;
        while (socket == null) {
            try {
                socket = new Socket("localhost", 8899);
                mClientSocket = socket;
                mPrintWriter = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
                uiHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
                Log.d(TAG, "connectTCPServer: connect server.");
            } catch (IOException e) {
                SystemClock.sleep(1000);
                e.printStackTrace();
                Log.d(TAG, "connectTCPServer: connect server failed,retry...");
            }
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!TCPClientActivity.this.isFinishing()) {
                final String msg = reader.readLine();
                Log.d(TAG, "connectTCPServer: receive message: " + msg);
                if (msg != null) {
                    uiHandler.obtainMessage(MESSAGE_RECEIVED_NEW_MSG, msg)
                            .sendToTarget();
                }
            }
            Log.d(TAG, "connectTCPServer: client quit.");
            reader.close();
            mPrintWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnSendView) {
            final String input = mEtInputView.getText().toString();
            if (!TextUtils.isEmpty(input) && mPrintWriter != null) {
                Log.d(TAG, "onClick: ");
                mPrintWriter.println(input);
                mEtInputView.setText("");
                mTvMessageView.setText(input);
            }
        }
    }

    static class UIHandler extends Handler {

        private WeakReference<TCPClientActivity> mActivityWeakReference;

        public UIHandler(TCPClientActivity activity, Looper looper) {
            super(looper);
            mActivityWeakReference = new WeakReference<TCPClientActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_RECEIVED_NEW_MSG:
                    mActivityWeakReference.get().mTvMessageView.setText(
                            mActivityWeakReference.get().mEtInputView.getText().toString() +
                                    (String) msg.obj);
                    break;
                case MESSAGE_SOCKET_CONNECTED:
                    mActivityWeakReference.get().mBtnSendView.setEnabled(true);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private ExecutorService getSingleThreadPool() {
        ExecutorService singleThreadPool = null;
        ThreadFactory namedThreadFactory = Executors.defaultThreadFactory();
        singleThreadPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(1024),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy());
        return singleThreadPool;
    }
}
