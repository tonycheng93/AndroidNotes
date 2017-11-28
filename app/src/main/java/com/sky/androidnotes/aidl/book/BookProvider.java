package com.sky.androidnotes.aidl.book;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * @author tonycheng
 * @date 2017/11/25
 */

public class BookProvider extends ContentProvider {

    private static final String TAG = "BookProvider";

    private static final String AUTHORITY = "com.sky.androidnotes.aidl.book.BOOK_PROVIDER";

    public static final Uri BOOK_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/book");

    public static final Uri USER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/user");

    public static final int BOOK_URI_CODE = 0;

    public static final int USER_URI_CODE = 1;

    public static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, "book", BOOK_URI_CODE);
        sUriMatcher.addURI(AUTHORITY, "user", USER_URI_CODE);
    }

    private Context mContext;
    private SQLiteDatabase mDb;

    private String getTableName(Uri uri) {
        String tableName = "";
        switch (sUriMatcher.match(uri)) {
            case BOOK_URI_CODE:
                tableName = BookContract.BOOK_TABLE_NAME;
                break;
            case USER_URI_CODE:
                tableName = BookContract.USER_TABLE_NAME;
                break;
            default:
                break;
        }
        return tableName;
    }

    @Override
    public boolean onCreate() {
        mContext = getContext();
        initDatabase();
        return true;
    }

    private void initDatabase() {
        mDb = new DbOpenHelper(mContext).getWritableDatabase();
        mDb.execSQL("DELETE FROM " + BookContract.BOOK_TABLE_NAME);
        mDb.execSQL("DELETE FROM " + BookContract.USER_TABLE_NAME);
        mDb.execSQL("INSERT INTO book VALUES(3,'Android');");
        mDb.execSQL("INSERT INTO book VALUES(4,'iOS');");
        mDb.execSQL("INSERT INTO book VALUES(5,'Html5');");
        mDb.execSQL("INSERT INTO user VALUES(1,'tom',10);");
        mDb.execSQL("INSERT INTO user VALUES(2,'jack',15);");
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final String tableName = getTableName(uri);
        if (TextUtils.isEmpty(tableName)) {
            throw new IllegalArgumentException("UnSupported Uri: " + uri);
        }
        return mDb.query(tableName, projection, selection, selectionArgs, null, null, sortOrder, null);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final String tableName = getTableName(uri);
        if (TextUtils.isEmpty(tableName)) {
            throw new IllegalArgumentException("UnSupported Uri: " + uri);
        }
        long row = mDb.insert(tableName, null, values);
        if (row > 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final String tableName = getTableName(uri);
        if (TextUtils.isEmpty(tableName)) {
            throw new IllegalArgumentException("UnSupported Uri: " + uri);
        }
        final int row = mDb.delete(tableName, selection, selectionArgs);
        if (row > 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }
        return row;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final String tableName = getTableName(uri);
        if (TextUtils.isEmpty(tableName)) {
            throw new IllegalArgumentException("UnSupported Uri: " + uri);
        }
        final int row = mDb.update(tableName, values, selection, selectionArgs);
        if (row > 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }
        return row;
    }
}
