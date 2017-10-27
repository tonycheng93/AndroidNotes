package com.sky.androidnotes.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class LocalService extends Service {

    private static final String TAG = "LocalService";

    private LocalBinder mBinder = null;

    public LocalService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null) {
            mBinder = new LocalBinder();
        }
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        Intent localIntent = new Intent(LocalService.this, RemoteService.class);
        localIntent.setAction("com.sky.intent.action.REMOTE_SERVICE");
        bindService(localIntent, mConnection, Context.BIND_AUTO_CREATE);
        return Service.START_STICKY;
    }

    private class LocalBinder extends IGuardAidlInterface.Stub {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            //default method,do nothing.
        }

        @Override
        public void evokeProcess() throws RemoteException {
            Log.d(TAG, "evokeProcess: bind success.");
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: RemoteService has been connected.");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: RemoteService has been killed.");
            Intent localIntent = new Intent(LocalService.this, RemoteService.class);
            localIntent.setAction("com.sky.intent.action.REMOTE_SERVICE");
            LocalService.this.startService(localIntent);
            LocalService.this.bindService(localIntent, mConnection, Context.BIND_ABOVE_CLIENT);
        }
    };
}
