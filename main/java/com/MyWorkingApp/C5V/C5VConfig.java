package com.MyWorkingApp.C5V;

import android.os.Environment;

/**
 * Created by softoz on 12/17/2015.
 */
public class C5VConfig {
    public  static final int REQUEST_PHOTO_CODE = 1;
    public static final int REQUEST_VIDEO_CODE = 2;
    public static final int REQUEST_AUDIO_CODE = 3;
    public static String DIRECTORY =Environment.getDataDirectory().toString()+ "/C5V/folder/";
    public static final String TAG = "C5V:";
    public static final String FILENAME = "C5V.xml";
    public static String RECORDS = "C5VRecords";
    public static String RECORD = "C5VRecord";
    public static String DATE = "Date";
    public static String PEN = "Pen";
    public static String AUDIO = "Audio";
    public static String PHOTO = "Photo";
    public static String VIDEO = "Video";
    public static String GPS = "Gps";
    public static String TEXT = "Text";
    public static String TITLE = "Title";

}
