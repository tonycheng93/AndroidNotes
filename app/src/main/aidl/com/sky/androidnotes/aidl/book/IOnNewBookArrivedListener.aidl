// IOnNewBookArrivedListener.aidl
package com.sky.androidnotes.aidl.book;

import com.sky.androidnotes.aidl.book.Book;

// Declare any non-default types here with import statements

interface IOnNewBookArrivedListener {

    void onNewBookArrived(in Book newBook);
}
