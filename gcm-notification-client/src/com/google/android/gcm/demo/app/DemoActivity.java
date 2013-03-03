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
package com.google.android.gcm.demo.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.android.gcm.demo.app.gcm.GcmRegistrationService;

import static com.google.android.gcm.demo.app.utils.Commons.DISPLAY_MESSAGE_ACTION;
import static com.google.android.gcm.demo.app.utils.Commons.EXTRA_MESSAGE;

/**
 * Main UI for the demo app.
 */
public class DemoActivity extends Activity {

    TextView display;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        display = (TextView) findViewById(R.id.display);
        registerReceiver(handleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
        startService(new Intent(this, GcmRegistrationService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.options_clear:
                display.setText(null);
                return true;
            case R.id.options_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(handleMessageReceiver);
        GcmRegistrationService.cancelPendingRegistration(this);
        super.onDestroy();
    }

    /**
     * Receiver that will handle gcm message
     */
    private final BroadcastReceiver handleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            display.append(intent.getExtras().getString(EXTRA_MESSAGE) + "\n");
        }
    };

}