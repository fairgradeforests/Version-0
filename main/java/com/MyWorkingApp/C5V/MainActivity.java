package com.MyWorkingApp.C5V;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ShareActionProvider;
import android.widget.ListView;


import java.io.File;
import java.util.ArrayList;


public class MainActivity extends ListActivity {
    private ActionManager mActionManager;

    private ShareActionProvider mShareActionProvider = null;

    public MainActivity() {

        Log.i(C5VConfig.TAG, "Instantiated new MainActivity");
    }

    @Override
    public void onResume() {
        super.onResume();
        //refresh the preferences
        mActionManager.loadData();
        refreshDisplay(false);
    }


    @Override
    public void onRestart() {
        super.onRestart();

        //refresh the preferences
        mActionManager.loadData();
        refreshDisplay(true);
    };

    @Override
    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }


    public void refreshDisplay(boolean clearSelection) {

        ListView list = (ListView) this.getListView();
        if (list != null) {
            C5VAdapter ma = (C5VAdapter) list.getAdapter();
            if (clearSelection)
                ma.selectedPositions.clear();
            //notify that the model changed
            ma.clear();
            ma.addAll(mActionManager.getRecords());
            ma.updateList();
        }
        if (mShareActionProvider!=null)
            mShareActionProvider.setShareIntent(getShareIntent());
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(C5VConfig.TAG, "called onCreate");
        //prevent screen from shutting down
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.main_view);
        //force brightness for the window
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = 1F;
        getWindow().setAttributes(layout);
        mActionManager = new ActionManager(this);
        mActionManager.loadData();


        ListView list = (ListView) this.getListView();
        list.setDividerHeight(2);
        list.setClickable(true);
        list.setAdapter(new C5VAdapter(this.getApplicationContext(), mActionManager.getRecords(), this));
    }



    public void myReload (){
        Intent intent = getIntent();
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.main_view, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        mShareActionProvider.setShareHistoryFileName("custom_share_history.xml");
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(getShareIntent());
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
//  when the button is clicked
            Intent i;
            i = new Intent(this, AboutActivity.class);
            startActivity(i);
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_all) {
//  when the button is clicked
            alertMessageDeleteAll();
        }
        if (id == R.id.action_delete) {
            deleteButtonOnClick(null);
        }
        if (id == R.id.action_switch) {

            ListView list = (ListView) this.getListView();
            if (list != null) {
                C5VAdapter ma = (C5VAdapter) list.getAdapter();
                ma.switchTitle();
                refreshDisplay(false);
            }
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_intro) {
//  when the button is clicked
            Intent i;
            i = new Intent(this, ScreenSlideActivity.class);
            startActivity(i);
        }
        //noinspection SimplifiableIfStatement
      /*  if (id == R.id.action_share) {
//  when the button is clicked

           if (BuildConfig.FREE) {
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(this, "Exported limited to one log only in Free version", duration);
            toast.show();
            }
            //todo create zip file then offer bluetooth email etc
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                File o = mActionManager.getExportZipFile();
                if (o!=null) {
                    //todo raise error message
                    Uri zipUri = Uri.fromFile(o);
                    //     sharingIntent.setType("zip");
                    sharingIntent.setType("message/rfc822");

                    sharingIntent.putExtra(Intent.EXTRA_STREAM, zipUri);
                    startActivity(Intent.createChooser(sharingIntent, "Share records"));
                }
        }
*/
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
//  when the button is clicked
            addButtonOnClick(null);
        }
        //BT share
      /*  if (id == R.id.menu_item_share) {
//  when the button is clicked
            File o = mActionManager.getExportZipFile();
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            if (o!=null) {
                //todo raise error message
                Uri zipUri = Uri.fromFile(o);
                sharingIntent.setType("message/rfc822");

                sharingIntent.putExtra(Intent.EXTRA_STREAM, zipUri);
                mShareActionProvider.setShareIntent(sharingIntent);
            }
        }
    */
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
//  when the exit button is clicked
            finishAffinity();
        }

        return super.onOptionsItemSelected(item);
    }
private Intent getShareIntent() {
    File o = mActionManager.getExportZipFile();
    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
    sharingIntent.setType("message/rfc822");
    if (o!=null) {
        //todo raise error message
        Uri zipUri = Uri.fromFile(o);
       // sharingIntent.setDataAndType(zipUri,getContentResolver().getType(zipUri));
        sharingIntent.setType("*/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, zipUri);
    }
    return sharingIntent;
};
    /**
     * when the map button is clicked
     */
    public void mapButtonOnClick(View v) {

        Intent i;
        i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }

    /**
     * when the + button is clicked
     */
    public void addButtonOnClick(View v) {
        final Intent j = new Intent(this, AddActivity.class);
        j.putExtra("myRecordId", -1);//create new index
        startActivity(j);
    }

    /**
     * when the - button is clicked
     */
    public void deleteButtonOnClick(View v) {
        deleteAllSelected();
        //refreshDisplay(true);
        myReload();
    }

    private void deleteAllSelected() {
       // loop thru the list
       ListView lv = this.getListView();
        C5VAdapter adapt = (C5VAdapter)lv.getAdapter();
        ArrayList<C5VRecord> tobeDeleted = new ArrayList<C5VRecord>();
        for (Integer pt : adapt.selectedPositions){
            tobeDeleted.add(mActionManager.getRecord(pt.intValue()));
        }
        adapt.selectedPositions.clear();
        for (C5VRecord rec : tobeDeleted){
            mActionManager.deleteRecord(rec);
        };
        mActionManager.saveData();
        adapt.clear();
        adapt.addAll(mActionManager.getRecords());
        adapt.updateList();
    }

    public void alertMessageDeleteAll() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        mActionManager.deleteAll();
                        //refreshDisplay(true);
                        myReload();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}

