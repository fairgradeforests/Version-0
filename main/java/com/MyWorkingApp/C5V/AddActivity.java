package com.MyWorkingApp.C5V;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends Activity {
    static String NO_APP = "No compatible app found";

    int count = 0;
    EditText textEdit = null;
    EditText titleEdit = null;
    private ActionManager myActionManager = null;
    private C5VRecord myRecord;

    private MyLocationListener mlocListener = new MyLocationListener();
    private LocationManager mlocManager;

    public AddActivity() {

        Log.i(C5VConfig.TAG, "Instantiated new MainActivity");
    }

    /**
     * Create a file Uri for saving an image or video
     */
    private Uri getOutputMediaFileUri(String ext) {
        return Uri.fromFile(getOutputMediaFile(ext));
    }

    private Uri currentURI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        myActionManager = ActionManager.getMyActionManager(this);
        textEdit = (EditText) findViewById(R.id.editText);
        textEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) changeText(textEdit);
            }
        });
        titleEdit = (EditText) findViewById(R.id.editTitle);
        titleEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) changeText(titleEdit);
            }
        });
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                0, mlocListener);
        int index = getIntent().getIntExtra("myRecordId", -1);
        if (index == -1) {
            //new record
            myRecord = new C5VRecord(this);
        } else {
            myRecord = myActionManager.getRecord(index);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        myActionManager.loadData();
        textEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) myRecord.setMyRecordText(textEdit.getText().toString());
            }
        });
    }

    public void OkOnClick(View v) {
        myRecord.setMyRecordText(textEdit.getText().toString());
        myActionManager.addRecord(myRecord);
        myActionManager.saveData();
        this.finish();
    }

    protected void changeText(View v) {

        if (v == textEdit) myRecord.setMyRecordText(textEdit.getText().toString());
        if (v == titleEdit) myRecord.setMyRecordTitle(titleEdit.getText().toString());
    }

    public void showMessage(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void recordPenOnClick(View v) {
        myRecord.setMyRecordText(textEdit.getText().toString());
        showMessage(getString(R.string.pen_notyet));
    }

    public void recordSoundOnClick(View v) {
        myRecord.setMyRecordText(textEdit.getText().toString());
        Intent intent = new Intent(
                MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        if (intent.resolveActivity(getPackageManager()) != null) {
            String title = getResources().getString(R.string.chooser_audio_title);

            startActivityForResult(
                    Intent.createChooser(intent, title), C5VConfig.REQUEST_AUDIO_CODE);
        } else {
            showMessage(NO_APP);
        }
    }

    /**
     * Get the uri of the captured file
     *
     * @return A Uri which path is the path of an image file, stored on the dcim folder
     */
    private Uri getImageUri() {
        try {
            Uri imgUri = Uri.fromFile(createImageFile());
            return imgUri;
        } catch (IOException e) {

            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(this, e.getLocalizedMessage(), duration);
            toast.show();
            return null;
        }
    }


    public Location getLocation() {
        Location l = mlocManager.getLastKnownLocation( mlocManager.getBestProvider(new Criteria(), false));
        return l;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        // Save a file: path for use with ACTION_VIEW intents
        return new File(storageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }


    public void recordPhotoOnClick(View v) {
        myRecord.setMyRecordText(textEdit.getText().toString());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            currentURI = getImageUri();
            String title = getResources().getString(R.string.chooser_photo_title);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, currentURI);

            Intent intentGallery = new Intent( Intent.ACTION_GET_CONTENT );
            intentGallery.setDataAndType(currentURI, "image/*" );

            Intent chooserIntent = Intent.createChooser(intent, title);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { intentGallery });
            startActivityForResult(chooserIntent, C5VConfig.REQUEST_PHOTO_CODE);
        } else {
            showMessage(NO_APP);
        }
    }

    public void recordVideoOnClick(View v) {
        myRecord.setMyRecordText(textEdit.getText().toString());
        count++;
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // Always use string resources for UI text.
        // This says something like "Share this photo with"
        String title = getResources().getString(R.string.chooser_video_title);

// Verify the intent will resolve to at least one activity
        if (intent.resolveActivity(getPackageManager()) != null) {
            Uri fileUri = getOutputMediaFileUri("mp4");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);

            Intent intentGallery = new Intent( Intent.ACTION_GET_CONTENT );
            intentGallery.setDataAndType(fileUri, "video/*");

            Intent chooserIntent = Intent.createChooser(intent, title);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { intentGallery });
            startActivityForResult(chooserIntent,
                    C5VConfig.REQUEST_VIDEO_CODE
            );
        } else {
            showMessage(NO_APP);
        }
    }

    private void galleryAddPic(String mCurrentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
int set= Color.parseColor("#99C181");
        if (requestCode == C5VConfig.REQUEST_PHOTO_CODE && resultCode == RESULT_OK) {
            if (data != null)
                if (data.getData() != null) currentURI = data.getData();
            String rp = getRealPathFromURI(currentURI);
            myRecord.setMyRecordPhoto(rp);
            ((ImageButton) findViewById(R.id.imageButtonPhoto)).setBackgroundColor(set);
            galleryAddPic(rp);//add to gallery
            Log.d("Photo capture", "Pic saved");
        }
        if (requestCode == C5VConfig.REQUEST_VIDEO_CODE && resultCode == RESULT_OK) {

            Uri vid = null;
            if (data != null) vid = data.getData();
            if (vid == null) vid = getOutputMediaFileUri("mp4");
            String rp = getRealPathFromURI(vid);
            myRecord.setMyRecordVideo(rp);
            ((ImageButton) findViewById(R.id.imageButtonVideo)).setBackgroundColor(set);
            Log.d("Video capture", "video saved");
        }
        if (requestCode == C5VConfig.REQUEST_AUDIO_CODE && resultCode == RESULT_OK) {
            Uri aud = null;
            if (data != null) aud = data.getData();
            String rp = getRealPathFromURI(aud);
            ((ImageButton) findViewById(R.id.imageButtonAudio)).setBackgroundColor(set);
                    myRecord.setMyRecordAudio(rp);
            Log.d("Audio capture", "Audio saved");
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        if (cursor == null)
        { // Source is Dropbox or other similar local file path
            return contentUri.getPath();
        }
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile(String ext) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "C5V");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("C5V", "failed to create directory");
                return null;
            }
        }
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "PHOTO_CAPTURED" + count + "." + ext);
        return mediaFile;
    }
}
