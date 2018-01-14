package com.paprika.teachme.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.paprika.teachme.R;

public class SearchActivity extends AppCompatActivity {

    private ListView list ;
    private ArrayAdapter<String> adapter ;

    String[] GetListItems(CharSequence searchString)//if ="" then return all
    {
        int count = 0;
        //list all at first
        for(int i = 0; i < Globals.Subjects.length; i++){
            String str1 = searchString.toString();
            String str2 = Globals.Subjects[i];
            if(searchString.length() <= 0 || str2.toLowerCase().contains(str1.toLowerCase()))
                count++;
            //and people, printers... todo
        }

        int j=0;
        String[] listItems = new String[count];
        for(int i = 0; i < Globals.Subjects.length; i++){
            String str1 = searchString.toString();
            String str2 = Globals.Subjects[i];
            if(searchString.length() <= 0 || str2.toLowerCase().contains(str1.toLowerCase())) {
                listItems[j] = Globals.Subjects[i];
                //and people, printers... todo
                j++;
            }
        }

        return listItems;
    }
    void UpdateView(CharSequence s)
    {
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, GetListItems(s));
        list.setAdapter(adapter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        list = (ListView) findViewById(R.id.lvSearch);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, GetListItems(""));
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startSearchResultsActivity(i);
            }
        });

        //---------------------------------
         EditText edit = findViewById(R.id.editSearch);
         edit.addTextChangedListener(new TextWatcher() {

             public void afterTextChanged(Editable s) {
             }

             public void beforeTextChanged(CharSequence s, int start,
                                           int count, int after) {
             }
             public void onTextChanged(CharSequence s, int start,
                                       int before, int count) {
                 UpdateView(s);
             }
         });

        Button back = findViewById(R.id.btnSearchBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
    }

    void startSearchResultsActivity(int clickedItem) {
        Intent i = new Intent(this, SearchResultsActivity.class);
        i.putExtra("CLICKED_ITEM", Globals.Subjects[clickedItem]);//todo people printers ...
        startActivity(i);//temp todo
        //finishAffinity();
    }
}