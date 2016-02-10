/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mobile.lapa.waitandsee;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onStart() {
        super.onStart();
    }

    //private AppCompatActivity thisActivity;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mTitleTextView;
    private TextView mMessageTextView;
//    private ImageView mMainImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //thisActivity = this;
        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                   // mMessageTextView.setText(getString(R.string.welcome_message));
                } else {
                    mMessageTextView.setText(getString(R.string.token_error_message));
                }
            }
        };
        mMessageTextView = (TextView) findViewById(R.id.messageTextView);
        mTitleTextView = (TextView) findViewById(R.id.titleTextView);
//        mMainImageView = (ImageView) findViewById(R.id.footerImageView);
//        mMainImageView.setBackgroundResource(R.mipmap.work09);

        Bundle b = getIntent().getExtras();
        if (b == null) {
            mTitleTextView.setText(getString(R.string.welcome_title));
            mMessageTextView.setText(getString(R.string.welcome_message));
        } else {
            String title = b.getString("TITLE_KEY");
            String message = b.getString("MESSAGE_KEY");
            mTitleTextView.setText(title);
            mMessageTextView.setText(message);
            Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.game_finished);
            RingtoneManager.getRingtone(this, defaultSoundUri).play();

        }

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
        //registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        //unregisterReceiver(mMessageReceiver);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


  /*  //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            // String message = intent.getStringExtra("message");
            String title = intent.getStringExtra("TITLE_KEY");
            String message = intent.getStringExtra("MESSAGE_KEY");
            mTitleTextView.setText(title);
            mMessageTextView.setText(message);

            //do other stuff here
        }
    };*/

}
