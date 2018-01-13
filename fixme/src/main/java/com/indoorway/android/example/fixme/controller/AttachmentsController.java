package com.indoorway.android.example.fixme.controller;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;

import com.indoorway.android.common.sdk.listeners.generic.Action1;
import com.indoorway.android.common.sdk.model.Coordinates;
import com.indoorway.android.common.sdk.model.IndoorwayMap;
import com.indoorway.android.example.fixme.BuildConfig;
import com.indoorway.android.example.fixme.R;
import com.indoorway.android.fragments.sdk.map.IndoorwayMapFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AttachmentsController implements Action1<Coordinates> {

    static final int CAMERA_REQUEST = 0;
    static final String CAMERA_PICTURE_NAME = "picture.jpg";
    static final String MAP_PICTURE_NAME = "map.jpg";

    Coordinates touchCoordinates;

    final AlertDialog.Builder alertDialog;

    final Activity activity;

    final MapFragmentProvider mapFragmentProvider;

    final AttachmentsListener attachmentsListener;

    public AttachmentsController(Activity activity, MapFragmentProvider mapFragmentProvider, AttachmentsListener attachmentsListener) {
        this.activity = activity;
        this.mapFragmentProvider = mapFragmentProvider;
        this.attachmentsListener = attachmentsListener;
        this.alertDialog = new AlertDialog.Builder(activity)
                .setMessage(R.string.do_you_want_to_send_a_report)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    public void showConfirmationDialog() {
        alertDialog
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mapFragmentProvider.getMapFragment().getMapView().getDisplay().makeScreenshot(new Action1<Bitmap>() {
                            @Override
                            public void onAction(Bitmap bitmap) {
                                onMapScreenshotTaken(bitmap);
                            }
                        });
                        dialog.dismiss();
                    }
                })
                .show();
    }

    void openCameraActivity() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                .putExtra(MediaStore.EXTRA_OUTPUT, getImageUri(CAMERA_PICTURE_NAME));
        activity.startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            onAttachmentsReady();
        }
    }

    private void onAttachmentsReady() {
        IndoorwayMap currentMap = mapFragmentProvider.getMapFragment().getCurrentMap();

        if (currentMap == null)
            return;

        attachmentsListener.onAttachmentsReady(currentMap, getAttachmentsUri(), touchCoordinates);
    }

    @NonNull
    private ArrayList<Uri> getAttachmentsUri() {
        ArrayList<Uri> result = new ArrayList<>();
        result.add(getImageUri(CAMERA_PICTURE_NAME));
        result.add(getImageUri(MAP_PICTURE_NAME));
        return result;
    }

    private Uri getImageUri(String filename) {
        File file = getImageFile(filename);
        return FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID, file);
    }

    @NonNull
    private File getImageFile(String filename) {
        File root = getReportsDirectory();
        return new File(root, filename);
    }

    private File getReportsDirectory() {
        File root = new File(activity.getFilesDir(), "reports");
        root.mkdir(); // TODO: check mkdir result
        return root;
    }

    private void onMapScreenshotTaken(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        byte[] bitmapdata = bos.toByteArray();

        File screenshotFile = getImageFile(MAP_PICTURE_NAME);
        try {
            FileOutputStream fos = new FileOutputStream(screenshotFile);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // unable to save bitmap
        }

        openCameraActivity();
    }

    @Override
    public void onAction(Coordinates coordinates) {
        touchCoordinates = coordinates;
        showConfirmationDialog();
    }

    public interface AttachmentsListener {

        void onAttachmentsReady(IndoorwayMap currentMap, List<Uri> attachmentsUri, Coordinates touchCoordinates);

    }

    public interface MapFragmentProvider {

        IndoorwayMapFragment getMapFragment();

    }

}
