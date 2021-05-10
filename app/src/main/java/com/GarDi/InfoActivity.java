package com.GarDi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.GarDi.Models.MaterialHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

public class InfoActivity extends AppCompatActivity {
    List<Float> datumvalueList = new ArrayList<>();
    List<String> resList = new ArrayList<>();
    String photoPath = null;
    ArrayAdapter<String> arrayAdapter = null;
    List<String> categories = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        categories.add("geometric");
        categories.add("vector");
        categories.add("design");
        photoPath = (String) getIntent().getSerializableExtra("photoPath");
        ListView lv = (ListView) findViewById(R.id.list_view);
        arrayAdapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,resList);
        lv.setAdapter(arrayAdapter);
        new ClarifaiTask().execute(new File(photoPath));

    }
    private class ClarifaiTask extends AsyncTask<File, Integer, Boolean> {
        protected Boolean doInBackground(File... images) {
            resList.clear();
// Connect to Clarifai using your API token
            ClarifaiClient client = new ClarifaiBuilder("28f7631e75464771b2f266ff6e0053e0").buildSync();
            List<ClarifaiOutput<Concept>> predictionResults;
// For each photo we pass, send it off to Clarifai
            for (File image : images) {
                predictionResults = client.getDefaultModels().generalModel().predict()
                            .withInputs(ClarifaiInput.forImage(image)).executeSync().get();
// Check if Clarifai thinks the photo contains the object we are looking for

                for (ClarifaiOutput<Concept> result : predictionResults) {
                    for (Concept datum : result.data()) {
                        if (!MaterialHandler.findSortingFromMaterial(datum.name()).matches("No sorting options found")) {
                            resList.add(String.format("%12s: %,.2f", MaterialHandler.findSortingFromMaterial(datum.name()), datum.value()));
                        }
                        /*
                        int i = 0;
                        while (i < categories.size()) {
                            Log.d("myTag", datum.name() + "" + categories.get(i));
                            if (datum.name().matches(categories.get(i))) {

                                i++;
                            } else {
                                i++;
                            }

                        }*/
                    }
                }
                if (resList.size() == 0){
                    resList.add("No sorting options found");
                }
                return true;
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
// Delete photo
            (new File(photoPath)).delete();
            photoPath = null;
// If image contained object, close the AlarmActivity
            if (result) {
                arrayAdapter.notifyDataSetChanged();
            } else {
            }
        }
    }
}
