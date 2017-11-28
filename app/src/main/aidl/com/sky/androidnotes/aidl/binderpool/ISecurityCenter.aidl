// ISecurityCenter.aidl
package com.sky.androidnotes.aidl.binderpool;

// Declare any non-default types here with import statements

interface ISecurityCenter {

    String encrypt( in String content);

    String decrypt( in String password);
}
