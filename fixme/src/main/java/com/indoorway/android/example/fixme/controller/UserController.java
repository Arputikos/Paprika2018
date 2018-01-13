package com.indoorway.android.example.fixme.controller;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.indoorway.android.common.sdk.IndoorwaySdk;
import com.indoorway.android.common.sdk.listeners.generic.Action1;
import com.indoorway.android.common.sdk.model.Coordinates;
import com.indoorway.android.common.sdk.model.IndoorwayObjectParameters;
import com.indoorway.android.common.sdk.model.RegisteredVisitor;
import com.indoorway.android.common.sdk.model.Visitor;
import com.indoorway.android.common.sdk.model.VisitorLocation;
import com.indoorway.android.common.sdk.task.IndoorwayTask;
import com.indoorway.android.map.sdk.listeners.OnObjectSelectedListener;
import com.indoorway.android.map.sdk.view.MapView;
import com.indoorway.android.map.sdk.view.drawable.figures.DrawableCircle;
import com.indoorway.android.map.sdk.view.drawable.layers.MarkersLayer;

import java.util.List;

public class UserController {
    private Context displayContext;
    private MarkersLayer usersLayer;
    private MapView mapView;    // fixme pewnie potrzeba akt przy zmianie mapy (pietra)

    private int usersDetected = 0;  // debug

    public UserController(Context displayContext/*, MapView mapView*/) {
        this.displayContext = displayContext;
        //this.mapView = mapView;

        Visitor user = new Visitor();
        user.setName("Pati");
        user.setMeta("Programowanie Android; sala 212; 14-16");
        user.setShareLocation(true);

        IndoorwaySdk.instance().visitor().setup(user); // opis: sets visitor details and register/updates it on server
    }

    public void drawUsersLocations(final MarkersLayer drawLayer) {
        Log.d("Rika ULoc", "Fetching users locations...");
        //final MarkersLayer usersPosLayer = mapView.getMarker().addLayer(10f);

        IndoorwaySdk.instance()
                .visitors()
                .locations()
                .setOnCompletedListener(new Action1<List<VisitorLocation>>() {
                    @Override
                    public void onAction(List<VisitorLocation> visitorLocations) {
                        // todo show locations on map
                        ++usersDetected;
                        int markerId = 1;
                        Log.d("Rika ULoc", "Users detected: " + visitorLocations.size());
                        for (final VisitorLocation userLocation : visitorLocations) {
                            if (userLocation != null) {
                                try {
                                    if (System.currentTimeMillis() - userLocation.getTimestamp().getTime() < 60000) {
                                        Log.d("Rika ULoc", "UserLoc detected: " + userLocation.toString());


                                        boolean userAlreadyFound = false;
                                        for (User userElem : UsersCollection.instance().getCollection()) {
                                            User user = new User();
                                            user.setVisitorLocation(userLocation);
                                            user.setUuid(userLocation.getVisitorUuid());
                                            UsersCollection.instance().add(user);
                                            //logUser(user);
                                        }

                                        DrawableCircle circle = new DrawableCircle(
                                                Integer.toString(markerId++),
                                                0.4f,       // circle radius
                                                Color.GREEN,    // color
                                                Color.GREEN,    // outline color
                                                0f,   // outline width
                                                new Coordinates(userLocation.getLat(), userLocation.getLon()));

                                        drawLayer.add(circle);
                                    }
                                } catch (Exception ex) {
                                    //Log.e("Rika ULoc", "Location is null");
                                }

                            } else {
                                Log.d("Rika ULoc", "User" + markerId + " is null");
                            }
                        }
                    }
                })
                .setOnFailedListener(new Action1<IndoorwayTask.ProcessingException>() {
                    @Override
                    public void onAction(IndoorwayTask.ProcessingException e) {
                        // todo message: failed
                        Log.d("Rika ULoc", "Location fetch failed");
                        Toast.makeText(displayContext, "Failed to load users locations :(", Toast.LENGTH_LONG).show();
                    }
                })
                .execute();

        IndoorwaySdk.instance()
                .visitors()
                .list()
                .setOnCompletedListener(new Action1<List<RegisteredVisitor>>() {
                    @Override
                    public void onAction(List<RegisteredVisitor> registeredVisitors) {
                        Log.d("Rika ULoc", "Inside visitors.list");
                        for (RegisteredVisitor userData : registeredVisitors) {

                            boolean userAlreadyFound = false;
                            for (User userElem : UsersCollection.instance().getCollection()) {
                                if (userElem.getUuid().equals(userData.getUuid())) {
                                    Log.d("Rika loop", "user loc found");
                                    userElem.setVisitorData(userData);
                                    userAlreadyFound = true;
                                    logUser(userElem);
                                    break;
                                }
                            }
                        }
                    }
                })
                .setOnFailedListener(new Action1<IndoorwayTask.ProcessingException>() {
                    @Override
                    public void onAction(IndoorwayTask.ProcessingException e) {
                        Log.d("Rika ULoc", "User fetch failed");
                        Toast.makeText(displayContext, "Failed to load users :(", Toast.LENGTH_LONG).show();
                    }
                }).execute();

        Visitor me = IndoorwaySdk.instance().visitor().me();
        Log.d("Rika me", "Me: " + me.toString());
    }

    public void logDetectedUsers() {
        Log.d("Rika usersList", "size = " + UsersCollection.instance().size());
        for (User user : UsersCollection.instance().getCollection()) {
            Log.d("Rika User", "Uuid=" + user.getUuid() + ", data: " + user.getVisitorData() + ", loc: " + user.getVisitorLocation());
        }
    }

    public void logUser(User user) {
        Log.d("Rika User", "Uuid=" + user.getUuid() + ", data: " + user.getVisitorData() + ", loc: " + user.getVisitorLocation());
    }
}
