package org.uni.pathfinder.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.uni.pathfinder.R;
import org.uni.pathfinder.RequestManager;

public class RoutePlanenErgebnisse extends AppCompatActivity {
    private RelativeLayout loader;
    private ListView listView;

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

        listView = findViewById(R.id.route_ergebnisse_list);

    }

    @Override
    public void onStart() {
        super.onStart();

        RequestManager.initialize(getApplicationContext());
        RequestManager.requestPath(this, RoutePlanen.getData(), listView, loader);
        RequestManager.flush();
    }
}
