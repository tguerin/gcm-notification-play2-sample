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
package com.google.android.gcm.demo.app.gcm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gcm.demo.app.R;
import com.google.android.gcm.demo.app.utils.ServerUtilities;

import static com.google.android.gcm.demo.app.utils.Commons.*;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GcmIntentService extends GCMBaseIntentService {

    private static final String TAG = "GcmIntentService";


    public GcmIntentService() {
        super("DemoGcm", SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, getString(R.string.gcm_registered));
        if (!ServerUtilities.register(context, registrationId)) {
            // At this point all attempts to register with the app server failed, so we need to unregister the device
            // from GCM - the app will try to register again when it is restarted. Note that GCM will send an
            // unregistered callback upon completion, but GcmIntentService.onUnregistered() will ignore it.
            GCMRegistrar.unregister(context);
        }
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            ServerUtilities.unregister(context, registrationId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        displayMessageAndNotifyUser(context, intent.getExtras().getString("message"));
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        displayMessageAndNotifyUser(context, getString(R.string.gcm_deleted, total));
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessageAndNotifyUser(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessageAndNotifyUser(context, getString(R.string.gcm_recoverable_error, errorId));
        return super.onRecoverableError(context, errorId);
    }

    public void displayMessageAndNotifyUser(Context context, String message) {
        displayMessage(context, message);
        notifyUser(context, message);
    }

}
