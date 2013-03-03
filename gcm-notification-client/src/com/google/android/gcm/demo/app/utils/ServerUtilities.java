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

import android.content.Context;
import android.util.Log;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gcm.demo.app.R;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import static com.google.android.gcm.demo.app.utils.Commons.*;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {

    private static final int MAX_ATTEMPTS = 2;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();


    private static final String REGISTRATION_URL = SERVER_ROOT_URL + "/registrations";

    /**
     * Register this account/device pair within the server.
     *
     * @return whether the registration succeeded or not.
     */
    public static boolean register(final Context context, final String registrationId) {
        Log.i(TAG, "registering device (registrationId = " + registrationId + ")");
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register it in the
        // demo server. As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                displayMessage(context, context.getString(R.string.server_registering, i, MAX_ATTEMPTS));
                // TODO check connection before call
                HttpResponse httpResponse = doHttpPut(REGISTRATION_URL, buildRegistationIdRequest(registrationId));
                if (httpResponse.getStatusLine().getStatusCode() != 200) {
                    if (!sleep(backoff)) return false;
                } else {
                    GCMRegistrar.setRegisteredOnServer(context, true);
                    displayMessage(context, context.getString(R.string.server_registered));
                    return true;
                }
            } catch (Exception e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(TAG, "Failed to register on attempt " + i, e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }

                if (!sleep(backoff)) return false;

                // increase backoff exponentially
                backoff *= 2;
            }
        }
        displayMessage(context, context.getString(R.string.server_register_error, MAX_ATTEMPTS));
        return false;
    }

    private static JSONObject buildRegistationIdRequest(String registrationId) throws JSONException {
        JSONObject regObject = new JSONObject();
        regObject.put("registrationId", registrationId);
        return regObject;
    }

    /**
     * Unregister this account/device pair within the server.
     */
    public static void unregister(final Context context, final String registrationId) {
        Log.i(TAG, "unregistering device (registrationId = " + registrationId + ")");
        try {
            doHttpDelete(REGISTRATION_URL + "/" + registrationId);
            GCMRegistrar.setRegisteredOnServer(context, false);
            displayMessage(context, context.getString(R.string.server_unregistered));
        } catch (IOException e) {
            // At this point the device is unregistered from GCM, but still registered in the server.
            // We could try to unregister again, but it is not necessary: if the server tries to send
            // a message to the device, it will get a "NotRegistered" error message and should unregister the device.
            displayMessage(context, context.getString(R.string.server_unregister_error, e.getMessage()));
        }
    }

    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param json     object to send.
     * @throws IOException propagated from PUT.
     */
    private static HttpResponse doHttpPut(String endpoint, JSONObject json) throws IOException {
        HttpPut putRequest = new HttpPut(endpoint);
        putRequest.setEntity(new StringEntity(json.toString()));
        putRequest.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        return executeRequest(putRequest);
    }


    /**
     * Issue a Delete request to the server.
     *
     * @param endpoint DELETE address.
     * @throws IOException propagated from DELETE.
     */
    private static HttpResponse doHttpDelete(String endpoint) throws IOException {
        return executeRequest(new HttpDelete(endpoint));
    }

    private static HttpResponse executeRequest(HttpRequestBase request) throws IOException {
        return new DefaultHttpClient().execute(request);
    }

    private static boolean sleep(long backoff) {
        try {
            Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
            Thread.sleep(backoff);
        } catch (InterruptedException e1) {
            // Activity finished before we complete - exit.
            Log.d(TAG, "Thread interrupted: abort remaining retries!");
            Thread.currentThread().interrupt();
            return false;
        }
        return true;
    }
}
