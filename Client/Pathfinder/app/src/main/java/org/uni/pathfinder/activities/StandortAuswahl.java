package org.uni.pathfinder.activities;

import android.app.Activity;
import android.location.Location;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.overlay.Circle;
import org.mapsforge.map.util.MapViewProjection;
import org.uni.pathfinder.LongitudeLatitude;
import org.uni.pathfinder.MyLocation;
import org.uni.pathfinder.R;
import org.uni.pathfinder.RequestManager;

import java.util.ArrayList;
import java.util.List;

public class StandortAuswahl extends AppCompatActivity {
    private MapView mapView;
    private Activity act;
    private static MyLocation myLocation;
    private static RelativeLayout loader;
    private static ImageView cross;
    private MapViewProjection projection;
    private ArrayList<String> parserList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standort_auswahl);

        Toolbar customToolbar = (Toolbar) findViewById(R.id.customToolbar);
        setSupportActionBar(customToolbar);
        //getSupportActionBar().setIcon(R.drawable.pathfinder_weiss_logo_small);

        // those 2 lines add the back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // status bar color
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        }

        cross = findViewById(R.id.img_crosshair);
        cross.setVisibility(View.INVISIBLE);

        loader = findViewById(R.id.loadingPanel);

        act = this;

        AndroidGraphicFactory.createInstance(getApplication());

        mapView = findViewById(R.id.map_view);

        try {
            mapView.setClickable(true);
            mapView.getMapScaleBar().setVisible(true);
            mapView.setBuiltInZoomControls(true);
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                @Override
                public void gotLocation(Location location) {
                    if((location.getLongitude()>=10)&&(location.getLongitude()<=14)){
                        if((location.getLatitude()>=53)&&(location.getLatitude()<=55)){
                            RequestManager.requestSector(act, LongitudeLatitude.MaxMinByLongitudeLatitudeRadius(location.getLatitude(),location.getLongitude(), 10000), mapView);
                            RequestManager.flush();
                        }
                    }
                }
            };

            projection = new MapViewProjection(mapView);

            Button auswahl = findViewById(R.id.button_auswahl);
            Button abbrechen = findViewById(R.id.button_abbrechen);

            auswahl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DEBUG1", "Auswahl clicked " + android.support.v7.appcompat.R.attr.actionBarSize);
                    projection = new MapViewProjection(mapView);
                    int[] coordinates = new int[2];
                    cross.getLocationInWindow(coordinates);
                    coordinates[0] += 50 -17;
                    coordinates[1] -= 60 -21;
                    LatLong ll = projection.fromPixels((double)coordinates[0], (double)coordinates[1]);
                    String text = ll.getLatitude() + ", " + ll.getLongitude();
                    text = "Wegpunkt hinzugefügt";
                    parserList.add(ll.getLatitude() + ", " + ll.getLongitude());
                    Toast.makeText(act, text, Toast.LENGTH_SHORT).show();

                }
            });

            abbrechen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RoutePlanen.setDataList(parserList);
                    act.finish();
                }
            });


            myLocation = new MyLocation();
            myLocation.getLocation(this, locationResult);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RelativeLayout getLoader() {
        return loader;
    }

    public static ImageView getCross(){
        return cross;
    }

    public static MyLocation getMyLocation(){
        return myLocation;
    }

    @Override
    public void onPause(){
        super.onPause();
        myLocation.cancelTimer();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        myLocation.cancelTimer();
    }

}
