// IMyService.aidl
package com.sky.androidnotes.aidl;

// Declare any non-default types here with import statements

import com.sky.androidnotes.aidl.Student;

interface IMyService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

     List<Student> getStudents();

     void addStudent(in Student student);
}
