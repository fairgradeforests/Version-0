package com.MyWorkingApp.C5V;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        String aboutText = getString(R.string.C5VAbout);
        aboutText += versionName;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_about);
        TextView tv = (TextView) findViewById(R.id.aboutId);
        tv.setText(aboutText);
        Linkify.addLinks(tv, Linkify.ALL);
    }


    public void nextButtonOnClick(View v) {
//  when the button is clicked

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.entry, menu);
        return super.onCreateOptionsMenu(menu);
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
        if (id == R.id.action_intro) {
//  when the button is clicked
            Intent i;
            i = new Intent(this, ScreenSlideActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

}
