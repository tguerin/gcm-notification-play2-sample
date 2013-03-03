/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gcm.demo.app.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.google.android.gcm.demo.app.DemoActivity;
import com.google.android.gcm.demo.app.R;

/**
 * Helper class providing methods and constants common to other classes in the app.
 */
public final class Commons {

    /**
     * Base URL of the Demo Server
     */
    public static final String SERVER_ROOT_URL = "YOUR_SERVER_ROOT_URL";

    /**
     * Google API project id registered to use GCM.
     */
    public static final String SENDER_ID = "YOUR_PROJECT_NUMBER";

    /**
     * Tag used on log messages.
     */
    public static final String TAG = "GCMDemo";

    /**
     * Intent used to display a message in the screen.
     */
    public static final String DISPLAY_MESSAGE_ACTION = "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    public static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p/>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        context.sendBroadcast(new Intent(DISPLAY_MESSAGE_ACTION).putExtra(EXTRA_MESSAGE, message));
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    public static void notifyUser(Context context, String message) {
        Notification notification = new Notification.Builder(context)
                .addAction(R.drawable.ic_stat_gcm, message, buildPendingIntent(context)) //
                .setSmallIcon(R.drawable.ic_stat_gcm) //
                .setWhen(System.currentTimeMillis()) //
                .setContentTitle(context.getString(R.string.notification_title)) //
                .setAutoCancel(true) //
                .build();

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(1, notification);
    }

    private static PendingIntent buildPendingIntent(Context context) {
        Intent notificationIntent = new Intent(context, DemoActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(context, 0, notificationIntent, 0);
    }
}
