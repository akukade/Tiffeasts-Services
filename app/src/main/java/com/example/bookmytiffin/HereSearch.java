package com.example.bookmytiffin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.LanguageCode;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.search.Place;
import com.here.sdk.search.SearchCallback;
import com.here.sdk.search.SearchEngine;
import com.here.sdk.search.SearchError;
import com.here.sdk.search.SearchOptions;
import com.here.sdk.search.TextQuery;

import java.util.ArrayList;
import java.util.List;

public class HereSearch extends AppCompatActivity {

    Button start;
    SearchEngine searchEngine;
    EditText searchquery;
    double lati,longi;
    ListView resultsListview;
    ArrayAdapter<String> adapter;
    ArrayList<String> searchresults;
    ArrayList<GeoCoordinates> coordinatesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_here_search);

        start = findViewById(R.id.start);
        searchquery = findViewById(R.id.searchquery);
        resultsListview = findViewById(R.id.searchresults);

        searchresults = new ArrayList<>();
        coordinatesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,searchresults);
        resultsListview.setAdapter(adapter);

        Current_Location current_location = (Current_Location) getApplication();
        lati = current_location.getCurr_lat();
        longi = current_location.getCurr_long();

        start.setOnClickListener(view -> {
            try {
                if(TextUtils.isEmpty(searchquery.getText())){
                    return;
                }

                searchEngine = new SearchEngine();

                SearchOptions searchOptions = new SearchOptions(LanguageCode.EN_US, 10);
                GeoCoordinates geoCoordinates = new GeoCoordinates(lati,longi);
                TextQuery query = new TextQuery(searchquery.getText().toString(), geoCoordinates);

                searchEngine.search(query, searchOptions, querySearchCallback);

            } catch (InstantiationErrorException e) {
                throw new RuntimeException("Initialization of SearchEngine failed: " + e.error.name());
            }
        });

        resultsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Toast.makeText(HereSearch.this,searchresults.get(pos),Toast.LENGTH_LONG).show();
                System.out.println("Lati " + coordinatesList.get(pos).latitude + " Longi" + coordinatesList.get(pos).longitude);
                //Access lat and long and address here after click
            }
        });
    }

    private SearchCallback querySearchCallback = new SearchCallback() {
        @Override
        public void onSearchCompleted(@Nullable SearchError searchError, @Nullable List<Place> list) {
            if (searchError != null) {
                Toast.makeText(HereSearch.this,searchError.toString(),Toast.LENGTH_LONG).show();
                System.out.println("Error" + searchError.toString());
                return;
            }

            if(list.isEmpty()){
                Toast.makeText(HereSearch.this,"No search result found",Toast.LENGTH_LONG).show();
                return;
            }
            searchresults.clear();
            coordinatesList.clear();
            System.out.println("List size" + list.size());
            // Add new marker for each search result on map.
            for (Place searchResult : list) {
                System.out.println(searchResult.getAddress());
                searchresults.add(searchResult.getAddress().addressText);
                coordinatesList.add(searchResult.getGeoCoordinates());
            }
            adapter.notifyDataSetChanged();
        }
    };

}