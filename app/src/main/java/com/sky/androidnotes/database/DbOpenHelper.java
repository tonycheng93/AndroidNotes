package com.sky.androidnotes.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tonycheng on 2017/6/2.
 */

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "DbOpenHelper";

    private static final String DATABASE_NAME = "User.db";

    private static final int DATABASE_VERSION = 1;

    public DbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(Db.UserTable.CREATE);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
