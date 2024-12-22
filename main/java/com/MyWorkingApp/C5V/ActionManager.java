package com.MyWorkingApp.C5V;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


/**
 * Created by softoz on 12/7/15.
 */
public class ActionManager {
    private static ActionManager myActionManager;
    private Activity mActivity;
    private MediaPlayer mediaPlayer;
    private ArrayList<C5VRecord> myRecords = new ArrayList<C5VRecord>();
    private C5VZipper myZipper = new C5VZipper();

    public ActionManager(Activity act) {
        mActivity = act;
    }

    public static ActionManager getMyActionManager(Activity act) {
        if (myActionManager == null) {
            myActionManager = new ActionManager(act);
            myActionManager.loadData();
        }
        return myActionManager;
    }

    public void addRecord(C5VRecord rec) {

        if (BuildConfig.FREE && (myRecords.size() >= 10)) {
            myRecords.remove(0);
        }
        myRecords.add(rec);
        Collections.sort(myRecords);
        saveData();
    }

    public void deleteRecord(C5VRecord rec) {
        myRecords.remove(rec);
        saveData();
    }

    public void deleteAll() {
        for (C5VRecord rec : myRecords) {
            rec.deleteAllFiles(mActivity);
        }
        myRecords.clear();
        saveData();
    }

    public void deleteAllSelected() {
        for (C5VRecord rec : myRecords) {
            if (rec.isSelected()) deleteSelectedRecord(rec);
        }
        saveData();
    }

    private void deleteSelectedRecord(C5VRecord rec) {
        rec.deleteAllFiles(mActivity);
        myRecords.remove(rec);
    }

    public void loadData() {
        myRecords = null;
        FileInputStream fis;
        try {

            fis = mActivity.getApplicationContext().openFileInput(C5VConfig.FILENAME);
            XmlPullParser parser = Xml.newPullParser();
            try {
                // auto-detect the encoding from the stream
                parser.setInput(fis, null);
                int eventType = parser.getEventType();
                C5VRecord currentRecord = null;
                boolean done = false;
                while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                    String name = null;
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            myRecords = new ArrayList<C5VRecord>();
                            break;
                        case XmlPullParser.START_TAG:
                            name = parser.getName();
                            if (name.equalsIgnoreCase(C5VConfig.RECORD)) {
                                currentRecord = new C5VRecord();
                            } else if (currentRecord != null) {
                                if (name.equalsIgnoreCase(C5VConfig.DATE)) {
                                    currentRecord.setMyRecordDate(new Date(Date.parse(parser.nextText())));
                                } else if (name.equalsIgnoreCase(C5VConfig.AUDIO)) {
                                    currentRecord.setMyRecordAudio(parser.nextText());
                                } else if (name.equalsIgnoreCase(C5VConfig.VIDEO)) {
                                    currentRecord.setMyRecordVideo(parser.nextText());
                                } else if (name.equalsIgnoreCase(C5VConfig.PHOTO)) {
                                    currentRecord.setMyRecordPhoto(parser.nextText());
                                } else if (name.equalsIgnoreCase(C5VConfig.TITLE)) {
                                    currentRecord.setMyRecordTitle(parser.nextText());
                                } else if (name.equalsIgnoreCase(C5VConfig.TEXT)) {
                                    currentRecord.setMyRecordText(parser.nextText());
                                } else if (name.equalsIgnoreCase(C5VConfig.GPS)) {
                                    currentRecord.setMyRecordGps(parser.nextText());
                                } else if (name.equalsIgnoreCase(C5VConfig.PEN)) {
                                    currentRecord.setMyRecordPen(parser.nextText());
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            name = parser.getName();
                            if (name.equalsIgnoreCase(C5VConfig.RECORD) &&
                                    currentRecord != null) {
                                myRecords.add(currentRecord);
                            } else if (name.equalsIgnoreCase(C5VConfig.RECORDS)) {
                                done = true;
                            }
                            break;
                    }
                    eventType = parser.next();
                }
            } catch (IOException e) {

                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(mActivity, e.getLocalizedMessage(), duration);
                toast.show();
            } finally {
                fis.close();
            }
        } catch (FileNotFoundException fe) {
            //for first run
            myRecords = new ArrayList<C5VRecord>();
        } catch (Exception e) {

            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(mActivity, e.getLocalizedMessage(), duration);
            toast.show();
            throw new RuntimeException(e);
        }
    }

    public void saveData() {
        try {
            String dir = mActivity.getApplicationContext().getFilesDir().getPath();
            FileOutputStream fos = mActivity.getApplicationContext().openFileOutput(C5VConfig.FILENAME, Context.MODE_PRIVATE);
            fos.write(myZipper.getRecordsXml(false, myRecords).getBytes());
            fos.close();
        } catch (IOException e) {

            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(mActivity, e.getLocalizedMessage(), duration);
            toast.show();
        }
    }

    public ArrayList<C5VRecord> getRecords() {
        return myRecords;
    }

    public C5VRecord getRecord(int index) {
        return myRecords.get(index);
    }

    public File getExportZipFile() {
        return myZipper.getExportZipFile(myRecords,mActivity);
    }

}
