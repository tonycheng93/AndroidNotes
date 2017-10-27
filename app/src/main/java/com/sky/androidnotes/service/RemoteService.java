package com.sky.androidnotes.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * RemoteService do nothing,just for evoke LocalService
 * to ensure LocalService is alive.
 */

public class RemoteService extends Service {

    private static final String TAG = "RemoteService";

    private RemoteBinder mBinder = null;

    public RemoteService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: remote service.");
        if (mBinder == null) {
            mBinder = new RemoteBinder();
        }
        try {
            mBinder.evokeProcess();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        Intent remoteIntent = new Intent(RemoteService.this, LocalService.class);
        remoteIntent.setAction("com.sky.intent.action.LOCAL_SERVICE");
        bindService(remoteIntent, mConnection, Context.BIND_ABOVE_CLIENT);
        return Service.START_STICKY;
    }

    private class RemoteBinder extends IGuardAidlInterface.Stub {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            //default method,do nothing.
        }

        @Override
        public void evokeProcess() throws RemoteException {
            Log.d(TAG, "evokeProcess: bind success.");
            bindService(new Intent(RemoteService.this, LocalService.class),
                    mConnection, Context.BIND_ABOVE_CLIENT);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: bind local service success.");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: Local service has been killed.");
            Intent remoteService = new Intent(RemoteService.this, LocalService.class);
            RemoteService.this.startService(remoteService);
            RemoteService.this.bindService(remoteService, mConnection, Context.BIND_ABOVE_CLIENT);
        }
    };
}
