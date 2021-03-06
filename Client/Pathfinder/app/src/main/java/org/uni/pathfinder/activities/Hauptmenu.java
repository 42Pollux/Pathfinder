package org.uni.pathfinder.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.uni.pathfinder.R;
import org.uni.pathfinder.RequestManager;
import org.uni.pathfinder.SessionStorage;
import org.uni.pathfinder.network.MapViewInitializer;
import org.uni.pathfinder.shared.PictureReference;
import org.uni.pathfinder.shared.ReferenceObject;
import org.uni.pathfinder.shared.TextReference;

import java.util.ArrayList;

public class Hauptmenu extends AppCompatActivity {
    public static final String SHAREDPREFKEY = "PathfinderPreferences";
    public static Context APPCONTEXT;
    public static boolean INIT = true;
    private Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hauptmenu);

        Toolbar customToolbar = (Toolbar) findViewById(R.id.customToolbar);
        setSupportActionBar(customToolbar);
        getSupportActionBar().setIcon(R.drawable.pathfinder_weiss_komplett);

        // those 2 lines add the back arrow
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // status bar color
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        }

        APPCONTEXT = getApplicationContext();
        act = this;

        FrameLayout frame_aktuelle_route = (FrameLayout) findViewById(R.id.menu_aktuelle_route);
        FrameLayout frame_neue_route = (FrameLayout) findViewById(R.id.menu_neue_route);
        FrameLayout frame_verlauf = (FrameLayout) findViewById(R.id.menu_verlauf);
        FrameLayout frame_meine_routen = (FrameLayout) findViewById(R.id.menu_meine_routen);
        FrameLayout frame_wandertipps = (FrameLayout) findViewById(R.id.menu_wandertipps);
        FrameLayout frame_einstellungen = (FrameLayout) findViewById(R.id.menu_einstellungen);

        frame_aktuelle_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.frame_click));
                if(SessionStorage.selectionMap!=null) {
                    Intent newAct = new Intent(Hauptmenu.this, AktuelleRoute.class);
                    Hauptmenu.this.startActivity(newAct);
                } else {
                    Toast.makeText(act, "Keine aktuelle Route vorhanden!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        frame_neue_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.frame_click));
                Intent newAct = new Intent(Hauptmenu.this, RoutePlanen.class);
                Hauptmenu.this.startActivity(newAct);
            }
        });

        frame_meine_routen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.frame_click));
                Intent newAct = new Intent(Hauptmenu.this, MeineRouten.class);
                Hauptmenu.this.startActivity(newAct);
            }
        });

        frame_verlauf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.frame_click));
                Intent newAct = new Intent(Hauptmenu.this, Verlauf.class);
                Hauptmenu.this.startActivity(newAct);
            }
        });


        frame_wandertipps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.frame_click));
            }
        });

        Log.d("Hauptmenu", "erstellt");
        frame_einstellungen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.frame_click));
            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();

        if(INIT) {
            MeineRouten.setSavedRoutes(MeineRouten.readSavedRoutes(), true);
        }
        SharedPreferences spref = getSharedPreferences(SHAREDPREFKEY, Context.MODE_PRIVATE);
        String uuid = spref.getString("key_uuid",  null);
        Log.d("DEBUG1", "uid set to: " + uuid);
        if((uuid==null)||uuid.equals("0000")){
            if(RequestManager.isOnline(getApplicationContext())){
                RequestManager.initialize(getApplicationContext());
                RequestManager.requestUUID(this);
                RequestManager.flush();
            }
        }

        INIT = false;
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

        return super.onOptionsItemSelected(item);
    }

}
