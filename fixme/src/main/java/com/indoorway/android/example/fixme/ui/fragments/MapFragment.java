package com.indoorway.android.example.fixme.ui.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.indoorway.android.common.sdk.listeners.generic.Action0;
import com.indoorway.android.common.sdk.listeners.generic.Action1;
import com.indoorway.android.common.sdk.model.Coordinates;
import com.indoorway.android.common.sdk.model.IndoorwayMap;
import com.indoorway.android.common.sdk.model.IndoorwayPosition;
import com.indoorway.android.example.fixme.R;
import com.indoorway.android.example.fixme.preferences.VisitorPreferences;
import com.indoorway.android.location.sdk.IndoorwayLocationSdk;
import com.indoorway.android.location.sdk.model.IndoorwayLocationSdkError;
import com.indoorway.android.map.sdk.view.MapView;
import com.indoorway.android.map.sdk.view.drawable.figures.DrawableCircle;
import com.indoorway.android.map.sdk.view.drawable.layers.MarkersLayer;

/**
 * Fragment with Indoorway Map.
 */
public class MapFragment extends Fragment {

    static final int REQUEST_PERMISSION_CODE = 1;

    VisitorPreferences visitorPreferences;

    View progressView;

    TextView tvStatus;

    MapView mapView;

    View fab;

    @Nullable
    IndoorwayPosition lastPosition;

    @Nullable
    IndoorwayMap currentMap;

    @Nullable
    OnMapScreenshotListener onMapScreenshotListener;

    @Nullable
    Action1<Coordinates> onClickListener;

    /**
     * Layer for markers.
     */
    MarkersLayer markerLayer;

    Action1<IndoorwayPosition> positionChangeListener;
    Action1<Float> headingChangeListener;
    Action1<IndoorwayLocationSdkError> stateErrorListener;

    public MapFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        visitorPreferences = new VisitorPreferences(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        startPositioningService();
    }

    @Override
    public void onPause() {
        fab.setVisibility(View.VISIBLE);
        stopPositioningService();
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onClickListener = null;
        onMapScreenshotListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        progressView = rootView.findViewById(R.id.progressView);
        tvStatus = rootView.findViewById(R.id.tvStatus);

        fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPositioningService();
            }
        });

        initMapView(rootView);
        loadMap();

        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // if permission is satisfied, run positioning service once again
        if (requestCode == REQUEST_PERMISSION_CODE && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startPositioningService();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Takes map screenshot and returns it result to the activity.
     */
    public void takeMapScreenshot() {
        mapView.getDisplay().makeScreenshot(new Action1<Bitmap>() {
            @Override
            public void onAction(Bitmap bitmap) {
                if (onMapScreenshotListener != null)
                    onMapScreenshotListener.onMapScreenshotTaken(bitmap);
            }
        });
    }

    public MapFragment setOnClickListener(@Nullable Action1<Coordinates> onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public MapFragment setOnMapScreenshotListener(@Nullable OnMapScreenshotListener onMapScreenshotListener) {
        this.onMapScreenshotListener = onMapScreenshotListener;
        return this;
    }

    /**
     * Gets current loaded map.
     *
     * @return
     */
    @Nullable
    public IndoorwayMap getCurrentMap() {
        return currentMap;
    }

    private void initMapView(View rootView) {
        // set callbacks
        mapView = rootView.findViewById(R.id.mapView);
        mapView.setOnMapLoadCompletedListener(new Action1<IndoorwayMap>() {
            @Override
            public void onAction(IndoorwayMap indoorwayMap) {
                markerLayer = mapView.getMarker().addLayer(11f);

                currentMap = indoorwayMap;
                // save last map for futher loading
                visitorPreferences.lastBuildingUuid.set(indoorwayMap.getBuildingUuid());
                visitorPreferences.lastMapUuid.set(indoorwayMap.getMapUuid());
                progressView.setVisibility(View.GONE);
            }
        });
        mapView.setOnMapLoadFailedListener(new Action0() {
            @Override
            public void onAction() {
                progressView.setVisibility(View.GONE);
            }
        });
        mapView.getTouch().setOnClickListener(new Action1<Coordinates>() {
            @Override
            public void onAction(Coordinates coordinates) {
                if (onClickListener != null)
                    onClickListener.onAction(coordinates);

                if (markerLayer != null) {
                    DrawableCircle circle = new DrawableCircle(
                            "x", // id
                            1f, // radius
                            ContextCompat.getColor(getContext(), R.color.error),
                            Color.TRANSPARENT,
                            0.0f, // outline width
                            coordinates
                    );
                    markerLayer.add(circle);
                }
            }
        });
    }

    private void loadMap() {
        progressView.setVisibility(View.VISIBLE);
        String buildingUuid = visitorPreferences.lastBuildingUuid.getOrDefault("");
        String mapUuid = visitorPreferences.lastMapUuid.getOrDefault("");

        if (!buildingUuid.isEmpty() && !mapUuid.isEmpty()) {
            // load last map
            tvStatus.setText(R.string.loading_a_map);
            mapView.load(buildingUuid, mapUuid);
        } else {
            // we need to obtain location first
            tvStatus.setText(R.string.looking_for_some_beacons);
        }
    }

    /**
     * Starts positioning service.
     */
    private void startPositioningService() {
        if (positionChangeListener == null)
            positionChangeListener = getPositionChangeListener();
        if (headingChangeListener == null)
            headingChangeListener = getHeadingChangeListener();
        if (stateErrorListener == null)
            stateErrorListener = getStateErrorListener();

        IndoorwayLocationSdk.instance().position().onChange().register(positionChangeListener);
        IndoorwayLocationSdk.instance().direction().onHeadingChange().register(headingChangeListener);
        IndoorwayLocationSdk.instance().state().onError().register(stateErrorListener);
    }

    private void stopPositioningService() {
        IndoorwayLocationSdk.instance().position().onChange().unregister(positionChangeListener);
        IndoorwayLocationSdk.instance().direction().onHeadingChange().unregister(headingChangeListener);
        IndoorwayLocationSdk.instance().state().onError().unregister(stateErrorListener);
    }

    private Action1<IndoorwayPosition> getPositionChangeListener() {
        return new Action1<IndoorwayPosition>() {
            @Override
            public void onAction(IndoorwayPosition position) {
                fab.setVisibility(View.GONE); // no need to show fab anymore
                mapView.getPosition().setPosition(position, true);
                lastPosition = position;
            }
        };
    }

    private Action1<Float> getHeadingChangeListener() {
        return new Action1<Float>() {
            @Override
            public void onAction(Float angle) {
                mapView.getPosition().setHeading(angle);
            }
        };
    }

    private Action1<IndoorwayLocationSdkError> getStateErrorListener() {
        return new Action1<IndoorwayLocationSdkError>() {
            @Override
            public void onAction(IndoorwayLocationSdkError error) {
                if (error instanceof IndoorwayLocationSdkError.BleNotSupported) {
                    // ble is not supported, indoor location won't work
                    showAlertDialog(R.string.sorry, R.string.ble_is_not_supported, null);
                } else if (error instanceof IndoorwayLocationSdkError.MissingPermission) {
                    // some permission is missing, ask for it and try again
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String[] permissions = {
                                ((IndoorwayLocationSdkError.MissingPermission) error).getPermission()
                        };
                        requestPermissions(permissions, REQUEST_PERMISSION_CODE);
                    }
                } else if (error instanceof IndoorwayLocationSdkError.BluetoothDisabled) {
                    // ask user to enable bluetooth
                    showAlertDialog(
                            R.string.enable_location,
                            R.string.in_order_to_find_your_position_enable_bluetooth,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showSettingsActivity(Settings.ACTION_BLUETOOTH_SETTINGS);
                                }
                            }
                    );
                } else if (error instanceof IndoorwayLocationSdkError.LocationDisabled) {
                    // ask user to  enable location
                    showAlertDialog(
                            R.string.enable_location,
                            R.string.in_order_to_find_your_position_enable_location,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showSettingsActivity(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                }
                            }
                    );
                }
            }
        };
    }

    private void showAlertDialog(int title, int message, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(R.string.ok, onClickListener)
                .setNegativeButton(R.string.cancel, new DissmissListener())
                .show();
    }

    private void showSettingsActivity(String action) {
        Intent settingsIntent = new Intent();
        settingsIntent.setAction(action);
        startActivity(settingsIntent);
    }

    private static class DissmissListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    public interface OnMapScreenshotListener {

        void onMapScreenshotTaken(Bitmap bitmap);

    }

}
