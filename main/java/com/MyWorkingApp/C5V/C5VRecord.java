
package com.MyWorkingApp.C5V;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by softoz on 12/6/2015.
 */
public class C5VRecord implements Comparable<C5VRecord>, Serializable {

    private Date myRecordDate = null;


    private boolean selected = false;
    private String myRecordPen = "";
    private String myRecordAudio = "";
    private String myRecordText = "";


    private String myRecordTitle = "";

    private String myRecordVideo = "";
    private String myRecordPhoto = "";
    private Location myRecordGps = null;

    public C5VRecord(AddActivity act) {
        setMyRecordDate();
        setMyRecordGps(act.getLocation());
    }

    public C5VRecord() {
    }

    public String getMyRecordPhoto() {
        return myRecordPhoto;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getMyRecordTitle() {
        return myRecordTitle;
    }
    public void setMyRecordPhoto(String myRecordPhoto) {
        this.myRecordPhoto = myRecordPhoto;
    }


    public String getMyRecordPen() {
        return myRecordPen;
    }

    public void setMyRecordPen(String myRecordPen) {
        this.myRecordPen = myRecordPen;
    }

    public String getMyRecordAudio() {
        return myRecordAudio;
    }

    public void setMyRecordAudio(String myRecordAudio) {
        this.myRecordAudio = myRecordAudio;
    }

    public Date getMyRecordDate() {
        return myRecordDate;
    }

    public void setMyRecordDate(Date myRecordDate) {
        this.myRecordDate = myRecordDate;
    }

    public void setMyRecordDate() {
        setMyRecordDate(new Date());
    }

    public String getMyRecordNameString() {
        return DateFormat.getInstance().format(getMyRecordDate());
    }


    public Location getMyRecordGps() {
        return myRecordGps;
    }


    public String getMyRecordText() {
        return myRecordText;
    }

    public void setMyRecordText(String myRecordText) {
        this.myRecordText = myRecordText;
    }
    public void setMyRecordTitle(String myRecordText) {
        this.myRecordTitle = myRecordText;
    }


    public String getMyRecordVideo() {
        return myRecordVideo;
    }

    public void setMyRecordVideo(String myRecordVideo) {
        this.myRecordVideo = myRecordVideo;
    }


    public void setMyRecordGps(Location myRecordGps) {
        this.myRecordGps = myRecordGps;
    }

    private void deleteUri(Uri uri, Context ctx) {
        File file = new File(uri.getPath());
        file.delete();
        if (file.exists()) {
            try {
                file.getCanonicalFile().delete();
                if (file.exists()) {
                    ctx.deleteFile(file.getName());
                }
            } catch (IOException e) {
            }
        }
    }

    public void deleteAllFiles(Context ctx) {
        if (myRecordPen.length() > 0) deleteUri(Uri.parse(myRecordPen), ctx);
        if (myRecordAudio.length() > 0) deleteUri(Uri.parse(myRecordAudio), ctx);
        if (myRecordVideo.length() > 0) deleteUri(Uri.parse(myRecordVideo), ctx);
        if (myRecordPhoto.length() > 0) deleteUri(Uri.parse(myRecordPhoto), ctx);
    }

    public int compareTo(C5VRecord comparesto) {
        Date compareage = ((C5VRecord) comparesto).getMyRecordDate();
        /* For Ascending order*/
        return this.getMyRecordDate().compareTo(compareage);
    }

    public void setMyRecordGps(String myRecordGpsString) {
        if (myRecordGpsString.length() == 0) {
            this.myRecordGps = null;
            return;
        }
        ;
        this.myRecordGps = new Location("");
        String[] separated = myRecordGpsString.split(",");

        double lat_start = 0;
        double lon_start = 0;

        try {

            lat_start = Double.parseDouble(separated[0]);
            lon_start = Double.parseDouble(separated[1]);

        } catch (NumberFormatException e) {
            Log.v("C5VRecord", "Convert to Double Failed : ");
        }
        if ((lat_start == 0) && (lon_start == 0)) myRecordGps = null;
        else {
            myRecordGps.setLatitude(lat_start);
            myRecordGps.setLongitude(lon_start);
        }
    }


    public String getGPSString() {
        if (myRecordGps == null) {
            return ("");
        }

        return ("" + myRecordGps.getLatitude() + ',' + myRecordGps.getLongitude());
    }
}
