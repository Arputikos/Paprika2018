package com.paprika.teachme.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.paprika.teachme.R;

public class FirstRunActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);

        // scan qr code
        findViewById(R.id.btnFRNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = findViewById(R.id.editName);
                EditText course = findViewById(R.id.editCourse);
                EditText year = findViewById(R.id.editYear);
                if(name.getText().length() > 0 &&  course.getText().length() > 0 && year.getText().length() > 0) {
                    Database.setName(name.getText().toString());
                    Database.setCourse(course.getText().toString());
                    Database.setYear(Integer.parseInt(year.getText().toString()));
                    //go to next
                    startMapActivity();
                }
                else
                {
                    Toast.makeText(FirstRunActivity.this, "Please fill all the fields",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Starts map activity.
     */
    void startMapActivity() {
        Intent i = new Intent(this, MapActivity.class);
        i.putExtra("MAP_ACTIVITY_STATE", Globals.MapActivityState.NORMAL.ordinal());
        startActivity(i);
        finishAffinity();
    }
}