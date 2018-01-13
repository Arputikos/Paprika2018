package com.indoorway.android.example.fixme.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.indoorway.android.common.sdk.IndoorwaySdk;
import com.indoorway.android.example.fixme.R;
import com.indoorway.android.example.fixme.controller.UserController;
import com.indoorway.android.example.fixme.preferences.VisitorPreferences;
import com.indoorway.android.qrcode.sdk.IndoorwayQrCodeSdk;

public class MainActivity extends AppCompatActivity {

    static final int QR_SCAN_REQUEST = 1;

    VisitorPreferences visitorPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        visitorPreferences = new VisitorPreferences(this);
        String token = visitorPreferences.token.getOrDefault("");

        // scan qr code
        findViewById(R.id.btnScan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // scan qr code
                IndoorwayQrCodeSdk.instance().startQrCodeActivity(MainActivity.this, QR_SCAN_REQUEST);
            }
        });

        // license
        findViewById(R.id.btnLicense).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://help.indoorway.com/docs/license-1"));
                startActivity(i);
            }
        });

        if (visitorPreferences.logged.getOrDefault(false) && !token.isEmpty()) {
            configureSdk(token);
            startMapActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QR_SCAN_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String code = IndoorwayQrCodeSdk.instance().getQrCodeFromIntent(data);

                if(code != null) {
                    configureSdk(code);

                    visitorPreferences.token.set(code);
                    visitorPreferences.logged.set(true);

                    startActivity(new Intent(MainActivity.this, MapActivity.class));
                    finishAffinity();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Configuration of Indoorway SDK
     *
     * @param token
     */
    void configureSdk(String token) {
        IndoorwaySdk.configure(token);
    }

    /**
     * Starts map activity.
     */
    void startMapActivity() {
        startActivity(new Intent(this, MapActivity.class));
        finishAffinity();
    }
}
