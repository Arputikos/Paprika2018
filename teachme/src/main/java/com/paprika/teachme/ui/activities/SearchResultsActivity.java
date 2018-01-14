package com.paprika.teachme.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.ListView;
import android.widget.TextView;

import com.paprika.teachme.R;

public class SearchResultsActivity extends AppCompatActivity {

    private ListView list ;
    private ArrayAdapter<String> adapter ;

    String[] GetListItems(CharSequence searchItem)//if ="" then return all
    {
        //todo backend - get people, printers etc and list here all which learn /sth/ or name ...

        String[] listItems = new String[3];
        listItems[0] = "Result 1";
        listItems[1] = "Result 2";
        listItems[2] = "Result 3";
        return listItems;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        String foundItem = "test";
        foundItem = getIntent().getStringExtra("CLICKED_ITEM");

        TextView title = findViewById(R.id.txtSearchResults);
        title.setText(foundItem);

        list = (ListView) findViewById(R.id.lvSearchResults);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, GetListItems(foundItem));
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startMapActivity();
            }
        });

        Button back = findViewById(R.id.btnSearchResultsBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
    }

    void startMapActivity() {
        Intent i = new Intent(this, MapActivity.class);
        i.putExtra("MAP_ACTIVITY_STATE", Globals.MapActivityState.SEARCH_RESULTS.ordinal());
        //todo pass where to go ...
        startActivity(i);
        finishAffinity();
    }
}