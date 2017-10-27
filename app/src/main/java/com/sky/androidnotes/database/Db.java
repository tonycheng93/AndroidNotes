package com.sky.androidnotes.database;

import android.provider.BaseColumns;

/**
 * Created by tonycheng on 2017/6/2.
 */

public class Db {

    private Db() {
    }

    public abstract static class UserTable implements BaseColumns {
        public static final String TABLE_NAME = "table_user";

        public static final String COLUMN_EMAIL = "email";

        public static final String COLUMN_USER_NAME = "user_name";

        public static final String COLUMN_PASSWORD = "password";

        public static final String COLUMN_SEX = "sex";

        public static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_EMAIL + " TEXT NOT NULL, " +
                        COLUMN_USER_NAME + " TEXT NOT NULL, " +
                        COLUMN_PASSWORD + " TEXT NOT NULL, " +
                        COLUMN_SEX + " INTEGER NOT NULL, " +
                        ");";
    }
}
