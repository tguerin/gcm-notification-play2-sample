package com.google.android.gcm.demo.app.gcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gcm.demo.app.R;
import com.google.android.gcm.demo.app.utils.ServerUtilities;

import static com.google.android.gcm.demo.app.utils.Commons.*;

public class GcmRegistrationService extends IntentService {

    public GcmRegistrationService() {
        super(GcmRegistrationService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        checkGcmInformationsValidity();

        if (!GCMRegistrar.isRegistered(this)) {
            registerDeviceOnGcm();
        } else {
            // Device is already registered on GCM, check server.
            registerDeviceOnServer();
        }
    }

    private void registerDeviceOnGcm() {
        GCMRegistrar.register(this, SENDER_ID);
    }

    private void registerDeviceOnServer() {
        if (GCMRegistrar.isRegisteredOnServer(this)) {
            // Skips registration.
            displayMessage(this, getString(R.string.already_registered));
        } else {
            if (!ServerUtilities.register(this, GCMRegistrar.getRegistrationId(this))) {
                // At this point all attempts to register with the app server failed, so we need to unregister the device
                // from GCM - the app will try to register again when it is restarted. Note that GCM will send an
                // unregistered callback upon completion, but GcmIntentService.onUnregistered() will ignore it.
                GCMRegistrar.unregister(this);
            }
        }
    }

    private void checkGcmInformationsValidity() {
        checkNotNull(SERVER_ROOT_URL, "SERVER_URL");
        checkNotNull(SENDER_ID, "SENDER_ID");

        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);

        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);
    }


    private void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException(getString(R.string.error_config, name));
        }
    }

    public static final void cancelPendingRegistration(Context context) {
        GCMRegistrar.onDestroy(context.getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
