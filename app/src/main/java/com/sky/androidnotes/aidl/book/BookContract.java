package com.sky.androidnotes.aidl.book;

import android.provider.BaseColumns;

/**
 *
 * @author tonycheng
 * @date 2017/11/27
 */

public final class BookContract {

    private BookContract() {
        //no instance
    }

    public static final String DATABASE_NAME = "bookProvider.db";

    public static final int DATABASE_VERSION = 1;

    public static final String BOOK_TABLE_NAME = "book";

    public static final String USER_TABLE_NAME = "user";

    public static final class BookTable implements BaseColumns {
        public static final String COLUMN_NAME = "name";

        public static final String CREATE_BOOK_TABLE =
                "CREATE TABLE IF NOT EXISTS " + BOOK_TABLE_NAME
                        + "(" + _ID + " INTEGER PRIMARY KEY,"
                        + COLUMN_NAME + " TEXT" + ")";

    }

    public static final class UserTable implements BaseColumns {
        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_SEX = "sex";

        public static final String CREATE_USER_TABLE =
                "CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME
                        + "(" + _ID + " INTEGER PRIMARY KEY,"
                        + COLUMN_NAME + " TEXT,"
                        + COLUMN_SEX + " INT" + ")";
    }
}
