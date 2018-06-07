package org.uni.pathfinder.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.uni.pathfinder.R;
import org.uni.pathfinder.RequestManager;

public class MeineRouten extends AppCompatActivity {
    private static RelativeLayout loader;
    private TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meine_routen);

        Toolbar customToolbar = (Toolbar) findViewById(R.id.customToolbar);
        setSupportActionBar(customToolbar);
        //getSupportActionBar().setIcon(R.drawable.pathfinder_weiss_logo_small);

        // those 2 lines add the back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        loader = findViewById(R.id.loadingPanelMeineRouten);
        txt = findViewById(R.id.txtViewMeineRouten);

    }


    @Override
    public void onStart(){
        super.onStart();

        RequestManager.initialize(getApplicationContext());
        RequestManager.requestHistory(this, txt, loader);
        RequestManager.flush();
    }
}
