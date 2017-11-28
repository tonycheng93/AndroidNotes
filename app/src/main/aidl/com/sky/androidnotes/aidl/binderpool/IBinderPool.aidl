// IBinderPool.aidl
package com.sky.androidnotes.aidl.binderpool;

// Declare any non-default types here with import statements

interface IBinderPool {
    IBinder queryBinder(in int binderCode);
}
