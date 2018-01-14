package com.paprika.teachme.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.ListView;
import android.widget.TextView;

import com.paprika.teachme.R;
import com.paprika.teachme.controller.User;
import com.paprika.teachme.controller.UsersCollection;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private ListView list ;
    private List<String> uuidOnList;

    List<String> GetListItems(CharSequence searchItem)//if ="" then return all
    {
        List<String> listItem = new ArrayList<>();
        uuidOnList = new ArrayList<>();
        //todo backend - get people, printers etc and list here all which learn /sth/ or name ...
        for(User user : UsersCollection.instance().getCollection()){
            if(user.getVisitorLocation()!= null){
                Data[] person = new Data[1];
               Database.LoadFromCloud(user.getVisitorData(), person);
               if(person == null) {
                   Log.e("aba", "the person is null");
                   continue;
               }
                //listItem.add(person[0].name);//", " + person[0].course
               // uuidOnList.add(user.getUuid());
            }
        }
//        String[] listItems = new String[3];
//        listItems[0] = "Result 1";
//        listItems[1] = "Result 2";
//        listItems[2] = "Result 3";
        return listItem;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        int foundItem = getIntent().getIntExtra("CLICKED_ITEM", 0);

        /*  for(User user : UsersCollection.instance().getCollection()){
            if(user.getVisitorLocation()!= null){
                   if( user.getUuid().equals(Database.getUuid()))
                       user.getVisitorData().setMeta(Database.getCourse() + "," + Database.getYear() + ","+ Database.getUuid() + "," + foundItem);
            }
        }  */

        TextView title = findViewById(R.id.txtSearchResults);
        title.setText(Globals.Subjects[foundItem]);

        list = (ListView) findViewById(R.id.lvSearchResults);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, GetListItems(Globals.Subjects[foundItem]));
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //search for selected person
                String UUID = "";
                if(uuidOnList.size() > i)
                    UUID = uuidOnList.get(i);
                startMapActivity(UUID);
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

    void startMapActivity(String UUID) {
        Intent i = new Intent(this, MapActivity.class);
        if(UUID.length() > 0) {
            i.putExtra("MAP_ACTIVITY_STATE", Globals.MapActivityState.SEARCH_RESULTS.ordinal());
            i.putExtra("TARGET_USER_ID", UUID);
        }
        //todo pass where to go ...
        startActivity(i);
        finishAffinity();
    }
}