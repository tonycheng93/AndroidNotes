package com.sky.androidnotes.aidl.book;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tonycheng on 2017/11/23.
 */

public class Book implements Parcelable {

    private int no;
    private String name;

    public Book(int no, String name) {
        this.no = no;
        this.name = name;
    }

    protected Book(Parcel in) {
        no = in.readInt();
        name = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(no);
        dest.writeString(name);
    }

    @Override
    public String toString() {
        return "Book{" +
                "no=" + no +
                ", name='" + name + '\'' +
                '}';
    }
}
