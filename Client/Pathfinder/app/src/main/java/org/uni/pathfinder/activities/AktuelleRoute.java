package org.uni.pathfinder.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import org.uni.pathfinder.LongitudeLatitude;
import org.uni.pathfinder.MyLocation;
import org.uni.pathfinder.R;
import org.uni.pathfinder.RequestManager;
import org.uni.pathfinder.SessionStorage;
import org.uni.pathfinder.shared.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AktuelleRoute extends AppCompatActivity {
    private Activity act;
    private static MyLocation myLocation;
    private static RelativeLayout loader;
    private static MapView mapView;
    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aktuelle_route);

        Toolbar customToolbar = (Toolbar) findViewById(R.id.customToolbar);
        setSupportActionBar(customToolbar);
        //getSupportActionBar().setIcon(R.drawable.pathfinder_weiss_logo_small);

        // those 2 lines add the back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        loader = findViewById(R.id.loadingPanelAktuelleRoute);
        View bottomSheet = findViewById(R.id.bottom_sheet);
        //ViewGroup.LayoutParams params = bottomSheet.getLayoutParams();
        //params.height = params.height - android.support.v7.appcompat.R.attr.actionBarSize;
        //bottomSheet.setLayoutParams(params);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(42);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        act = this;

        AndroidGraphicFactory.createInstance(getApplication());

        mapView = findViewById(R.id.currentRouteMapView);

        // get data
        ArrayList<ReferenceObject> refs = SessionStorage.referenceObjects;
        ArrayList<String> path_data = SessionStorage.currentPath;   // TODO use this instead

        MyLocation.LocationResult locationResult = null;
        try {
            mapView.setClickable(true);
            mapView.getMapScaleBar().setVisible(true);
            mapView.setBuiltInZoomControls(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        locationResult = new MyLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                if((location.getLongitude()>=10)&&(location.getLongitude()<=14)){
                    if((location.getLatitude()>=53)&&(location.getLatitude()<=55)){
                        RequestManager.initialize(getApplicationContext());
                        RequestManager.requestSector(act, LongitudeLatitude.getRouteSurroundingBoundingBox(SessionStorage.currentPath, 1000), mapView);
                        RequestManager.flush();
                    }
                }
            }
        };

        // build reference objects
        LinearLayout linear = findViewById(R.id.reference_layout);
        RequestManager.initialize(getApplicationContext());
        Log.d("AktRoute", "Building " + refs.size() + " reference objects");
        for(ReferenceObject r : refs) {
            Log.d("AktRoute", "Type " + r.getClassName());
            if(r.getClassName().equals(PictureReference.class.toString())) {
                PictureReference pic = (PictureReference) r;
                Log.d("AktRoute", "Picture id:" + r.getReferenceID());
                ImageView imgv = new ImageView(this);
                imgv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300));
                linear.addView(imgv);
                imgv.setPadding(2, 2, 2, 0);
                RequestManager.requestImage(this, (long)r.getReferenceID(), imgv);
            } else if(r.getClassName().equals(TextReference.class.toString())) {
                TextReference txt = (TextReference) r;
                Log.d("AktRoute", "Text id:" + r.getReferenceID());
                TextView txtv = new TextView(this);
                txtv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                linear.addView(txtv);
                RequestManager.requestText(this, (long)r.getReferenceID(), txtv);
            }
        }
        myLocation = new MyLocation();
        myLocation.getLocation(this, locationResult);



    }

    public static RelativeLayout getLoader() {
        return loader;
    }

    public static MapView getMapView() {
        return mapView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_akt, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_offline_karten) {
            return true;
        }
        if (id == R.id.action_test) {
            return true;
        }

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
