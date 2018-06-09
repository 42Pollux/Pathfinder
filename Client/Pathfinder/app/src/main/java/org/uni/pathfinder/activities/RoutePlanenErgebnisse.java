package org.uni.pathfinder.activities;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_route) {
            return true;
        }
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
