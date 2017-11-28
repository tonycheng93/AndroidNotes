package com.sky.androidnotes.aidl.binderpool.impl;

import android.os.RemoteException;

import com.sky.androidnotes.aidl.binderpool.ICompute;

/**
 * @author tonycheng
 * @date 2017/11/28
 */

public class ComputeImpl extends ICompute.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {
        return (a + b);
    }
}
