package com.sky.androidnotes.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tonycheng on 2017/9/27.
 */

public class Student implements Parcelable {

    public static final int SEX_MALE = 1;
    public static final int SEX_FEMALE = 2;

    public int sno;
    public String name;
    public int sex;
    public int age;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.sno);
        dest.writeString(this.name);
        dest.writeInt(this.sex);
        dest.writeInt(this.age);
    }

    public Student() {
    }

    protected Student(Parcel in) {
        this.sno = in.readInt();
        this.name = in.readString();
        this.sex = in.readInt();
        this.age = in.readInt();
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel source) {
            return new Student(source);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    @Override
    public String toString() {
        return "Student{" +
                "sno=" + sno +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", age=" + age +
                '}';
    }
}
