package org.uni.pathfinder.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.uni.pathfinder.ListViews.ListViewEntryVerlauf;
import org.uni.pathfinder.R;
import org.uni.pathfinder.RequestManager;

import java.util.ArrayList;

public class Verlauf extends AppCompatActivity {
    private static RelativeLayout loader;
    private TextView txt;
    private ListView listView;
    private ArrayList<ListViewEntryVerlauf> dataModels;

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
}
