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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyGcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String title = data.getString("title");
//        "event": "clone done",  <GIT_CLONE, BUILD, RUN, HANA_VOTER, IWINFRA_VOTER>
//                "title": "clone done",
//                "message" : "clone done after 10 minutes"

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(title, message);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param title
     * @param message GCM message received.
     */
    private void sendNotification(String title, String message) {

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = pm.newWakeLock((PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "YourServie");
        mWakeLock.acquire();

      /*  // ------ Update main activity text
        // This function will create an intent. This intent must take as parameter the "unique_name" that you registered your activity with
        Intent intentRecieveer = new Intent("unique_name");

        intentRecieveer.putExtra("TITLE_KEY", title);
        intentRecieveer.putExtra("MESSAGE_KEY", message);

        //send broadcast
        sendBroadcast(intentRecieveer);
*/

        // ------- Send notification to the android system
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra("TITLE_KEY", title);
        intent.putExtra("MESSAGE_KEY", message);

        startActivity(intent);
        mWakeLock.release();

//        //intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0  Request code , intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.devx_logo); //new Bitmap(R.drawable.sap_logo);
//
////        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
//
//        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.game_finished);
//        //Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.sap_logo)
//                .setLargeIcon(icon)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent)
//
//                .setDefaults(Notification.DEFAULT_ALL) // requires VIBRATE permission;
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText(message))
//
//                 ;

/*        new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setContentTitle(getString(R.string.notification))
                .setContentText(getString(R.string.ping))
                .setDefaults(Notification.DEFAULT_ALL) // requires VIBRATE permission
        *//*
         * Sets the big view "big text" style and supplies the
         * text (the user's reminder message) that will be displayed
         * in the detail area of the expanded notification.
         * These calls are ignored by the support library for
         * pre-4.1 devices.
         *//*
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .addAction (R.drawable.ic_stat_dismiss,
                        getString(R.string.dismiss), piDismiss)
                .addAction (R.drawable.ic_stat_snooze,
                        getString(R.string.snooze), piSnooze);*/

//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
