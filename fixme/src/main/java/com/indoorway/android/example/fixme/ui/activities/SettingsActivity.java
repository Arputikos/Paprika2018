package com.indoorway.android.example.fixme.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.indoorway.android.example.fixme.R;

public class SettingsActivity extends AppCompatActivity {

    AlertDialog alertDialog;
    int selectedSubjectID = 0;

    void ShowDialog()
    {
        if(alertDialog != null)
            alertDialog.show();
    }

    void LoadData()
    {
        findViewById(R.id.switchActive).setActivated(Database.isActive());
        findViewById(R.id.switchNavigate).setActivated(Database.isNavigate());
        selectedSubjectID = Database.getSubjectIDx();

        UpdateView();
    }
    void UpdateView()//after change of subject and active
    {
        Switch active = findViewById(R.id.switchActive);
        Button setSubject = findViewById(R.id.btnSetSubject);
        boolean check = active.isChecked();

        TextView tv = findViewById(R.id.txtLearningNow);
        if(check)
            tv.setText(getResources().getString(R.string.learningNow)+" "+Globals.Subjects[selectedSubjectID]);
        else
            tv.setText(getResources().getString(R.string.txtNoSubject)+" ("+getResources().getString(R.string.selected)+" "+Globals.Subjects[selectedSubjectID]+")");

        setSubject.setEnabled(check);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("Choose a subject");

        //selectedSubjectID = load...todo

        builder.setItems(Globals.Subjects, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedSubjectID = which;
                Database.setSubjectIDx(selectedSubjectID);
                UpdateView();
                //selectedSubjectID = ...  and save todo
            }
        });

    // create and show the alert dialog
        alertDialog = builder.create();

        Switch active = findViewById(R.id.switchActive);
        Switch navigate = findViewById(R.id.switchNavigate);
        Button setSubject = findViewById(R.id.btnSetSubject);

        LoadData();

        setSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog();
            }

        });
        active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Database.setActive(isChecked);
                Button setSubject = findViewById(R.id.btnSetSubject);
                setSubject.setEnabled(isChecked);
                UpdateView();
            }
        });

        navigate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Database.setNavigate(isChecked);
                //UpdateView();
            }
        });

        Button back = findViewById(R.id.btnSettingsBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
    }
}