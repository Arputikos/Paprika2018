package com.paprika.teachme.controller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class ReportController {

    final Activity activity;

    public ReportController(Activity activity) {
        this.activity = activity;
    }

    public void submit(String subject, String text, List<Uri> attachmentsUri, String chooserText) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE)
                .putExtra(Intent.EXTRA_SUBJECT, subject)
                .putExtra(Intent.EXTRA_TEXT, text)
                .putExtra(Intent.EXTRA_STREAM, new ArrayList<>(attachmentsUri))
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"test@example.com"});

        intent.setType("image/jpeg");
        activity.startActivity(Intent.createChooser(intent, chooserText));
    }

}
