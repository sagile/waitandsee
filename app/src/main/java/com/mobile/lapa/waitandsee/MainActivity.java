/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import android.speech.tts.TextToSpeech;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Locale;

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
    private TextView mUserNameEditText;
    private Button mSubmitButton;
    private SharedPreferences settings;
    private String userName;

    // TextToSpeech
    private static TextToSpeech textToSpeech;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (!sentToken) {
                    mMessageTextView.setText(getString(R.string.token_error_message));
                }
            }
        };

        mMessageTextView = (TextView) findViewById(R.id.messageTextView);
        mTitleTextView = (TextView) findViewById(R.id.titleTextView);
        mUserNameEditText = (EditText) findViewById(R.id.userNameEditText);
        mSubmitButton = (Button) findViewById(R.id.submitButton);

        mUserNameEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mSubmitButton.performClick();
                    return true;
                }
                return false;
            }
        });

        if (this.textToSpeech == null) {
            textToSpeech = new TextToSpeech(this.getApplicationContext(),
                    new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status != TextToSpeech.ERROR) {
                                textToSpeech.setLanguage(Locale.ENGLISH); //getDefault());
                            }
                        }
                    });
        }

        Bundle b = getIntent().getExtras();
        String title = "";
        String message = "";
        if (b != null) {
            title = b.getString("TITLE_KEY");
            message = b.getString("MESSAGE_KEY");
        }

        if (title == "" && message == "") { //(b == null) {
            mTitleTextView.setText(getString(R.string.welcome_title));
            mMessageTextView.setText(getString(R.string.welcome_message));
        } else {
            mTitleTextView.setText(title);
            mMessageTextView.setText(message);
            playSound(title);
        }

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        // ------- User name ------------
        settings = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        userName = settings.getString("USER_NAME", "");
        if (userName != "") {
            // user name already set
            hideUserFields();
        }
        setUserTitle();
    }

    private void setUserTitle() {
        String activityTitle = getText(R.string.app_name) + " [" + userName + "]";
        setTitle(activityTitle);
    }

    private void playSound(String title) {
//        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.game_finished);
//        RingtoneManager.getRingtone(this, defaultSoundUri).play();
        try {
            Thread.sleep(1300);
            speakText(title);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }

    }

    private void hideUserFields() {
        mUserNameEditText.setVisibility(View.GONE);
        mSubmitButton.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
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

    private void speakText(String text) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, null);
        } else {
            textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
        }
    }

    public void submitUserName(View view) {
        // Set user name
        userName = mUserNameEditText.getText().toString();
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("USER_NAME", userName);
        editor.commit();
        hideUserFields();

        Context context = getApplicationContext();
        CharSequence text = "User name " + userName + " set successfully";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        setUserTitle();
    }
}
