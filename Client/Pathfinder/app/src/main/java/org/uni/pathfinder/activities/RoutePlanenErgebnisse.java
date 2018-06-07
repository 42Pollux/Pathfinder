package org.uni.pathfinder.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.uni.pathfinder.ListViewEntry;
import org.uni.pathfinder.R;
import org.uni.pathfinder.RequestManager;
import org.uni.pathfinder.shared.XMLObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RoutePlanenErgebnisse extends AppCompatActivity {
    private static RelativeLayout loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_planen_ergebnisse);

        Toolbar customToolbar = (Toolbar) findViewById(R.id.customToolbar);
        setSupportActionBar(customToolbar);
        //getSupportActionBar().setIcon(R.drawable.pathfinder_weiss_logo_small);

        // those 2 lines add the back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        loader = findViewById(R.id.loadingPanelErgebnisse);
        TextView txt = findViewById(R.id.testTxtView);

        ListView listView = findViewById(R.id.route_ergebnisse_list);

        RequestManager.initialize(getApplicationContext());
        RequestManager.requestPath(this, RoutePlanen.getData(), txt);
        RequestManager.flush();
    }
}
