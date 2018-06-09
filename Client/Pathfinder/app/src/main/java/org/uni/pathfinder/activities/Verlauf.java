package org.uni.pathfinder.activities;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.uni.pathfinder.ListViews.ListViewEntry;
import org.uni.pathfinder.R;
import org.uni.pathfinder.RequestManager;

import java.util.ArrayList;

public class Verlauf extends AppCompatActivity {
    private static RelativeLayout loader;
    private TextView txt;
    private ListView listView;
    private ArrayList<ListViewEntry> dataModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verlauf);

        Toolbar customToolbar = (Toolbar) findViewById(R.id.customToolbar);
        setSupportActionBar(customToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        loader = findViewById(R.id.loadingPanelVerlauf);

        //txt = findViewById(R.id.txtViewVerlauf);
        listView = findViewById(R.id.listViewVerlauf);

    }


    @Override
    public void onStart(){
        super.onStart();

        RequestManager.initialize(getApplicationContext());
        RequestManager.requestHistory(this, listView, loader);
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
