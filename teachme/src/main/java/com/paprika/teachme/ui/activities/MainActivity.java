package com.paprika.teachme.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.indoorway.android.common.sdk.IndoorwaySdk;
import com.indoorway.android.common.sdk.model.Visitor;
import com.paprika.teachme.R;
import com.paprika.teachme.preferences.VisitorPreferences;
import com.indoorway.android.qrcode.sdk.IndoorwayQrCodeSdk;

public class MainActivity extends AppCompatActivity {

    static final int QR_SCAN_REQUEST = 1;

    VisitorPreferences visitorPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //loads from storage to memory, all user things
        Database.SetContext(this);
        Database.LoadFromStorage();

        visitorPreferences = new VisitorPreferences(this);
        String token = visitorPreferences.token.getOrDefault("");

        Visitor user = new Visitor();
        //user.setMeta(userCourse + "," + userYear + "," + " " + "," + " ");
        user.setName("name");
        user.setMeta("meta");
        //Database.SaveToCloud();//set meta
        user.setShareLocation(true);
        String id = user.getUuid();

        IndoorwaySdk.instance().visitor().setup(user);

        Database.LoadFromStorage();
        Database.SaveToCloud(user);
        Log.e("encode1",user.getMeta());

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
            startNextActivity();
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

                    startNextActivity();
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
    void startNextActivity() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstRun = preferences.getBoolean("first-run", true);

        if(firstRun)
        {
            Log.e("a","FIRST");
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean("first-run", false);
            edit.commit();

            startActivity(new Intent(this, FirstRunActivity.class));
            finishAffinity();
            return;
        }
        Log.e("a","NOT FIRST");
        Intent i = new Intent(this, MapActivity.class);
        i.putExtra("MAP_ACTIVITY_STATE", Globals.MapActivityState.NORMAL.ordinal());
        startActivity(i);
        finishAffinity();
    }
}