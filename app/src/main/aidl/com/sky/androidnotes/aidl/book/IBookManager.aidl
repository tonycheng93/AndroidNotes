// IBookManager.aidl
package com.sky.androidnotes.aidl.book;

import com.sky.androidnotes.aidl.book.Book;
import com.sky.androidnotes.aidl.book.IOnNewBookArrivedListener;

// Declare any non-default types here with import statements

interface IBookManager {

    void addBook(in Book book);

    List<Book> getBookList();

    void registerListener(IOnNewBookArrivedListener listener);

    void unRegisterListener(IOnNewBookArrivedListener listener);
}
