package com.indoorway.android.example.fixme.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.indoorway.android.common.sdk.listeners.generic.Action1;
import com.indoorway.android.common.sdk.model.Coordinates;
import com.indoorway.android.common.sdk.model.IndoorwayMap;
import com.indoorway.android.example.fixme.R;
import com.indoorway.android.example.fixme.controller.AttachmentsController;
import com.indoorway.android.example.fixme.controller.ReportController;
import com.indoorway.android.fragments.sdk.map.IndoorwayMapFragment;
import com.indoorway.android.fragments.sdk.map.MapFragment;
import com.indoorway.android.map.sdk.view.MapView;
import com.indoorway.android.map.sdk.view.drawable.figures.DrawableCircle;
import com.indoorway.android.map.sdk.view.drawable.layers.MarkersLayer;

import java.util.List;

public class MapActivity extends AppCompatActivity implements AttachmentsController.MapFragmentProvider, AttachmentsController.AttachmentsListener,
        MapFragment.OnMapFragmentReadyListener {

    @Nullable
    MarkersLayer dotLayer;

    AttachmentsController attachmentsController;
    ReportController reportController;

    void LoadData()
    {
        ToggleButton tgl = findViewById(R.id.btnMapActive);
        tgl.setChecked(Database.isActive());

        TextView t = findViewById(R.id.txtMapSubject);
        String s = Globals.Subjects[Database.getSubjectIDx()];
        if(!Database.isActive())
            s = getResources().getString(R.string.txtNoSubject);
        t.setText(s);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        attachmentsController = new AttachmentsController(this, this, this);
        reportController = new ReportController(this);


        findViewById(R.id.fabSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSettingsActivity();
            }
        });
        findViewById(R.id.fabSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearchActivity();
            }
        });

        ToggleButton active = findViewById(R.id.btnMapActive);
        active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Database.setActive(isChecked);
                LoadData();//reset all
                //UpdateView();
            }
        });

        LoadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        attachmentsController.onActivityResult(requestCode, resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public IndoorwayMapFragment getMapFragment() {
        return (IndoorwayMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
    }

    @Override
    public void onAttachmentsReady(IndoorwayMap currentMap, List<Uri> attachmentsUri, Coordinates touchCoordinates) {
        reportController.submit(
                getString(R.string.maintenance_report),
                getString(
                        R.string.building_map,
                        currentMap.getBuildingName(),
                        currentMap.getMapName(),
                        touchCoordinates.getLatitude(),
                        touchCoordinates.getLongitude()
                ),
                attachmentsUri,
                getString(R.string.share_your_report)
        );
    }

    @Override
    public void onMapFragmentReady(MapFragment mapFragment) {
        MapView mapView = mapFragment.getMapView();
        mapView.setOnMapLoadCompletedListener(new Action1<IndoorwayMap>() {
            @Override
            public void onAction(IndoorwayMap indoorwayMap) {
                dotLayer = getMapFragment().getMapView().getMarker().addLayer(20f);
            }
        });
        mapView.getTouch().setOnClickListener(new Action1<Coordinates>() {
            @Override
            public void onAction(Coordinates coordinates) {
                if (dotLayer != null) {
                    DrawableCircle circle = new DrawableCircle(
                            "x", // id
                            1f, // radius
                            ContextCompat.getColor(MapActivity.this, R.color.error),
                            coordinates
                    );
                    dotLayer.add(circle);

                    attachmentsController.onAction(coordinates);
                }
            }
        });

        // start positioning service
        mapFragment.startPositioningService();
    }


    void startSettingsActivity() {
        startActivity(new Intent(this, SettingsActivity.class));
        //finishAffinity();
    }
    void startSearchActivity() {
        startActivity(new Intent(this, SearchActivity.class));
        //finishAffinity();
    }
}
