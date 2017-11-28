package com.sky.androidnotes.aidl.binderpool.impl;

import android.os.RemoteException;

import com.sky.androidnotes.aidl.binderpool.ISecurityCenter;

/**
 * @author tonycheng
 * @date 2017/11/28
 */

public class SecurityCenterImpl extends ISecurityCenter.Stub {

    private static final char SECRET_CODE = '^';

    @Override
    public String encrypt(String content) throws RemoteException {
        char[] chars = content.toCharArray();
        for (int i = 0, length = chars.length; i < length; i++) {
            chars[i] ^= SECRET_CODE;
        }
        return new String(chars);
    }

    @Override
    public String decrypt(String password) throws RemoteException {
        return encrypt(password);
    }
}
