package com.sky.androidnotes.aidl.book;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sky.androidnotes.R;

import java.util.List;

public class BookManagerActivity extends AppCompatActivity {

    private static final String TAG = "BookManagerActivity";

    private static final int MESSAGE_NEW_BOOK_ARRIVED = 0x100;

    private IBookManager mBookManager = null;

    private UIHandler mHandler = new UIHandler();

    private Binder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            //binder died.
            if (mBookManager == null) {
                return;
            }
            mBookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            //reconnect the binder
            Intent intent = new Intent(BookManagerActivity.this, BookManagerService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            mBookManager = IBookManager.Stub.asInterface(service);

            if (mBookManager == null) {
                Log.d(TAG, "onServiceConnected: sorry,you can not connect remote service," +
                        "please ensure you have the right permission.");
            }

            try {
                mBookManager.asBinder().linkToDeath(mDeathRecipient, 0);
                final List<Book> bookList = mBookManager.getBookList();
                Log.d(TAG, "onServiceConnected: get book list = " + bookList.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            Book newBook = new Book(3, "Android进阶之光");
            try {
                mBookManager.addBook(newBook);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "onServiceConnected: newBook = " + newBook.toString());

            try {
                final List<Book> bookList = mBookManager.getBookList();
                Log.d(TAG, "onServiceConnected: bookList = " + bookList.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            try {
                mBookManager.registerListener(mListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBookManager = null;
            Log.d(TAG, "onServiceDisconnected: thread name = " + Thread.currentThread().getName());
            Log.d(TAG, "onServiceDisconnected: the binder died.");
        }
    };

    private IOnNewBookArrivedListener mListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, newBook)
                    .sendToTarget();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_manager);

        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        if (mBookManager != null && mBookManager.asBinder().isBinderAlive()) {
            Log.d(TAG, "onDestroy: unregister listener = " + mListener);
            try {
                mBookManager.unRegisterListener(mListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        super.onDestroy();
    }

    private static class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.d(TAG, "handleMessage: received new book = " + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
