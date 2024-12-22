package com.MyWorkingApp.C5V;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.widget.Toast;

import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.ServerManagedPolicy;

/**
 * NOTES ON USING THIS LICENSE FILE IN YOUR APPLICATION:
 * 1. Define the package
 * of you application above
 * 2. Be sure your public key is set properly  @BASE64_PUBLIC_KEY
 * 3. Change your SALT using random digits
 * 4. Under AllowAccess, Add your previously used MainActivity
 * 5. Add this activity to
 * your manifest and set intent filters to MAIN and LAUNCHER
 * 6. Remove Intent Filters from previous main activity
 */
public class LicenseCheck extends Activity {
    private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
        @Override
        public void allow(int reason) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
// Should allow user access.
            startMainActivity();

        }

        @Override
        public void applicationError(int errorCode) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            // This is a polite way of saying the developer made a mistake
            // while setting up or calling the license checker library.
            // Please examine the error code and fix the error.
            String errorCodeName="Unknown error";
            switch (errorCode) {
                case LicenseCheckerCallback.ERROR_INVALID_PACKAGE_NAME:
                    errorCodeName = "ERROR_INVALID_PACKAGE_NAME";
                    break;
                case LicenseCheckerCallback.ERROR_NON_MATCHING_UID:
                    errorCodeName = "ERROR_NON_MATCHING_UID";
                    break;
                case LicenseCheckerCallback.ERROR_NOT_MARKET_MANAGED:
                    errorCodeName = "ERROR_NOT_MARKET_MANAGED";
                    break;
                case LicenseCheckerCallback.ERROR_CHECK_IN_PROGRESS:
                    errorCodeName = "ERROR_CHECK_IN_PROGRESS";
                    break;
                case LicenseCheckerCallback.ERROR_INVALID_PUBLIC_KEY:
                    errorCodeName = "ERROR_INVALID_PUBLIC_KEY";
                    break;
                case LicenseCheckerCallback.ERROR_MISSING_PERMISSION:
                    errorCodeName = "ERROR_MISSING_PERMISSION";
                    break;
                default:
                    break;
            }

            //toast("Error: " + errorCodeName);
            startMainActivity();

        }

        @Override
        public void dontAllow(int reason ) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
                if (BuildConfig.DEBUG) startMainActivity();
            else {
                    // Should not allow access. In most cases, the app should assume
                    // the user has access unless it encounters this. If it does,
                    // the app should inform the user of their unlicensed ways
                    // and then either shut down the app or limit the user to a
                    // restricted set of features.
                    // In this example, we show a dialog that takes the user to Market.
                    showDialog(0);
                }
        }
    }
    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAop0jFgb8y59d9ZKmX5wTC7EtqIlzd4NB5qT0bMBD1yCTs1uGud5uwXxbJS/OdWdm8NHT073IFBKncTZutb5bD66DVHYuXidPI8gHAvu5DOPUMotfRTZ2HuD9bl80BTmbsKQafkN4+gB4l6ZmY25+QqbouI48AtNIvThszbNPDk/rHuh/RifTqF02aGNXHXh3BI3sLAlg5wNEkZg7W+j82utCcmO1qljkS/LziHsVdBTzXyI7xAGRCHW48ZPphmC0/6FfUA19lIFWjiUVxqtrbko65w/ldHSHhRC6cgfcMKGi6u9W4KccQqGJTyUQRNfal2d5pC4RdXi0qX4y56ApGwIDAQAB";

       // Generated 20 random bytes put them here.
    private static final byte[] SALT = new byte[] {
            -46, 45, 30, -128, -56, -46, 74, -64, 51, 88, -95,
            -45, 77, -117, 23, -113, -11, -42, -64, 89
    };

    private LicenseChecker mChecker;

// A handler on the UI thread.

    private LicenseCheckerCallback mLicenseCheckerCallback;
    private void doCheck() {
        mChecker.checkAccess(mLicenseCheckerCallback);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if ((BuildConfig.FREE)||(BuildConfig.AMAZON)) {

            startMainActivity();
        } else {

// Try to use more data here. ANDROID_ID is a single point of attack.
            String deviceId = Secure.getString(getContentResolver(),
                    Secure.ANDROID_ID);

// Library calls this when it's done.
            mLicenseCheckerCallback = new MyLicenseCheckerCallback();
// Construct the LicenseChecker with a policy.
                mChecker = new LicenseChecker(this, new ServerManagedPolicy(this,
                        new AESObfuscator(SALT, getPackageName(), deviceId)),
                        BASE64_PUBLIC_KEY);
            doCheck();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
// We have only one dialog.
        return new AlertDialog.Builder(this)
                .setTitle("Application Not Licensed")
                .setCancelable(false)
                .setMessage(
                        "This application is not licensed. Please purchase it from Android Market")
                .setPositiveButton("Buy App",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Intent marketIntent = new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("http://market.android.com/details?id="
                                                + getPackageName()));
                                startActivity(marketIntent);
                                finish();
                            }
                        })
                .setNegativeButton("Exit",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        }).create();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
       if (mChecker!=null) mChecker.onDestroy();
    }

    private void startMainActivity() {
        startActivity(new Intent(this, EntryActivity.class));
        finish();
    }

    public void toast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

}