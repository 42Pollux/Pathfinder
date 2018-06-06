package org.uni.pathfinder.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import org.uni.pathfinder.ListViewEntry;
import org.uni.pathfinder.R;
import org.uni.pathfinder.RequestManager;
import org.uni.pathfinder.shared.XMLObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RoutePlanenErgebnisse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_planen_ergebnisse);


        ListView listView = findViewById(R.id.route_ergebnisse_list);

        ArrayList<ListViewEntry> entries = new ArrayList<>();

        TextView txt = findViewById(R.id.testTxtView);

        RequestManager.initialize(getApplicationContext());
        RequestManager.requestPath(this, RoutePlanen.getData(), txt);
        RequestManager.flush();
    }
}
