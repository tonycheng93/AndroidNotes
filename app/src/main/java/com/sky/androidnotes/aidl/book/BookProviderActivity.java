package com.sky.androidnotes.aidl.book;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sky.androidnotes.R;

/**
 * @author tonycheng
 */
public class BookProviderActivity extends AppCompatActivity {

    private static final String TAG = "BookProviderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_provider);

        Uri bookUri = Uri.parse("content://com.sky.androidnotes.aidl.book.BOOK_PROVIDER/book");
        ContentValues values = new ContentValues();
        values.put(BookContract.BookTable._ID, 6);
        values.put(BookContract.BookTable.COLUMN_NAME, "Android设计模式");
        getContentResolver().insert(bookUri, values);

        Cursor bookCursor = getContentResolver().query(bookUri, new String[]{BookContract.BookTable._ID,
                BookContract.BookTable.COLUMN_NAME}, null, null, null);
        if (bookCursor == null) {
            return;
        }
        while (bookCursor.moveToNext()) {
            final int bookId = bookCursor.getInt(bookCursor.getColumnIndexOrThrow(BookContract.BookTable._ID));
            final String bookName = bookCursor.getString(bookCursor.getColumnIndexOrThrow(BookContract.BookTable.COLUMN_NAME));
            Book book = new Book(bookId, bookName);
            Log.d(TAG, "onCreate: book = " + book.toString());
        }
        bookCursor.close();
    }
}
