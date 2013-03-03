package com.google.android.gcm.demo.app.gcm;

import android.content.Context;
import com.google.android.gcm.GCMBroadcastReceiver;

public class GcmBroadcastReceiver extends GCMBroadcastReceiver {

    public GcmBroadcastReceiver() {
        super();
    }

    @Override
    protected String getGCMIntentServiceClassName(Context context) {
        return GcmIntentService.class.getCanonicalName();
    }
}
