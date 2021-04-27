package com.GarDi;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {
    List<String> resList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        resList = (ArrayList<String>) getIntent().getSerializableExtra("LIST");
        ListView lv = (ListView) findViewById(R.id.list_view);
        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,resList);
        lv.setAdapter( arrayAdapter);

    }
}
