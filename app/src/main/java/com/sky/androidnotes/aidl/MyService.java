package com.sky.androidnotes.aidl;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.sky.androidnotes.R;
import com.sky.androidnotes.TestActivity;

import java.util.ArrayList;
import java.util.List;

public class MyService extends Service {

    private static final String TAG = "MyService";

    private static final String PACKAGE_NAME = "com.sky.androidnotes";

    private NotificationManagerCompat mNotificationManager;
    private boolean mCanRun = true;
    private final List<Student> mStudents = new ArrayList<>();

    //这里实现了aidl中的抽象函数
    private final IMyService.Stub mBinder = new IMyService.Stub() {
        @Override
        public List<Student> getStudents() throws RemoteException {
            synchronized (mStudents) {
                return mStudents;
            }
        }

        @Override
        public void addStudent(Student student) throws RemoteException {
            synchronized (mStudents) {
                if (!mStudents.contains(student)) {
                    mStudents.add(student);
                }
            }
        }

        //在这里可以做权限认证，return false意味着客户端的调用就会失败，比如下面，只允许包名为com.example.test的客户端通过，
        //其他apk将无法完成调用过程
        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String packageName = null;
            String[] packages = MyService.this.getPackageManager().getPackagesForUid(getCallingUid());
            if (packages != null && packages.length > 0) {
                packageName = packages[0];
            }
            Log.d(TAG, "onTransact: " + packageName);
            if (!PACKAGE_NAME.equals(packageName)) {
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }
    };

    public MyService() {
    }

    @Override
    public void onCreate() {
        Thread thread = new Thread(null, new ServiceWork(), "BackgroundService");
        thread.start();

        synchronized (mStudents) {
            for (int i = 0; i < 6; i++) {
                Student student = new Student();
                student.name = "student#" + i;
                student.age = i * 5;
                mStudents.add(student);
            }
        }
        mNotificationManager = NotificationManagerCompat.from(this);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: intent = " + intent.toString());
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mCanRun = false;
        super.onDestroy();
    }

    private void displayNotificationMessage(String message) {
        Notification notification = new Notification(R.mipmap.ic_launcher_round, message, System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_ALL;
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, TestActivity.class), 0);
//        notification.setLatestEventInfo(this, "我的通知", message,
//                contentIntent);
        mNotificationManager.notify(R.string.app_name, notification);
    }

    private class ServiceWork implements Runnable {

        long counter = 0;

        @Override
        public void run() {
            while (mCanRun) {
                Log.d(TAG, "run: counter = " + counter);
                counter++;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
