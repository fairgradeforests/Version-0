package com.MyWorkingApp.C5V;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private ActionManager mActionManager;

    GoogleMap googleMap;
    SharedPreferences sharedPreferences;
    int locationCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mActionManager = new ActionManager((Activity) this);
        mActionManager.loadData();
        super.onCreate(savedInstanceState);

        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else { // Google Play Services are available

            setContentView(R.layout.activity_maps);
            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            fm.getMapAsync(this);
        }
    }

    private void drawMarker(C5VRecord rec) {
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        double lat = rec.getMyRecordGps().getLatitude();
        double lng = rec.getMyRecordGps().getLongitude();

        // Setting latitude and longitude for the marker
        markerOptions.position(new LatLng(lat, lng));
        markerOptions.title(DateFormat.getDateTimeInstance().format(rec.getMyRecordDate()));
        markerOptions.snippet(DateFormat.getDateTimeInstance().format(rec.getMyRecordDate()));

        // Adding marker on the Google Map
        googleMap.addMarker(markerOptions);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap gMap) {

        // Getting GoogleMap object from the fragment
        googleMap = gMap;
        mActionManager.loadData();
        // Enabling MyLocation Layer of Google Map
        googleMap.setMyLocationEnabled(true);
        double lat = 0;
        double lng = 0;

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker m) {
                Date kd =null;
                try {
                    kd = DateFormat.getDateTimeInstance().parse(m.getTitle());
                } catch (Exception e)
                {

                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(getApplication(), e.getLocalizedMessage(), duration);
                    toast.show();
                }
                if (kd==null) return true;
                for (C5VRecord rec : mActionManager.getRecords()) {
                    if (rec.getMyRecordDate().getTime() == kd.getTime()) {
                        // Drawing marker on the map
                        // DialogFragment.show() will take care of adding the fragment
                        // in a transaction.  We also want to remove any currently showing
                        // dialog, so make our own transaction and take care of that here.
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Fragment prev = getFragmentManager().findFragmentByTag("C5VCustomDialog");
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);

                        // Create and show the dialog.
                        C5VDetailsDialog newFragment = C5VDetailsDialog.newInstance(rec);
                        newFragment.show(ft, "C5VCustomDialog");
                    }
                }

                return true;
            }

        });
        for (C5VRecord rec : mActionManager.getRecords()) {
            if (rec.getMyRecordGps() != null) {
                // Drawing marker on the map
                drawMarker(rec);
                lat=rec.getMyRecordGps().getLatitude();
                lng=rec.getMyRecordGps().getLongitude();
            }
        }
        if ((lat!=0) && (lng!=0)) {
            // Moving CameraPosition to last clicked position
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));

            // Setting the zoom level in the map on last position  is clicked
         //
        }
    }

}
