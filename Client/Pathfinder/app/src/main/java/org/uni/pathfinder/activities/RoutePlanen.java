package org.uni.pathfinder.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.mapsforge.core.model.LatLong;
import org.uni.pathfinder.R;
import org.uni.pathfinder.RequestManager;
import org.uni.pathfinder.shared.XMLObject;

import java.util.ArrayList;
import java.util.List;

public class RoutePlanen extends AppCompatActivity {
    private static Activity act;
    private static List<String> dataList;
    private static TextView start, mid, end;
    private static XMLObject data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_erstellen);

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

        start = findViewById(R.id.txtStartpoint);
        end = findViewById(R.id.txtEndpoint);
        mid = findViewById(R.id.txtMidpoint);
        Button berechnen = findViewById(R.id.button_berechnen);
        act = this;

        if(dataList==null){
            dataList = new ArrayList<String>();
        } else {
            if(!dataList.isEmpty()){
                loadData();
            }
        }

        RequestManager.initialize(getApplicationContext());

        Switch showMap = findViewById(R.id.collapseMap);
        showMap.setChecked(false);
        showMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if ( ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

                        ActivityCompat.requestPermissions( act, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                                10 );
                    } else {
                        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                            buildAlertMessageNoGps();
                        } else {
                            Intent newAct = new Intent(RoutePlanen.this, StandortAuswahl.class);
                            RoutePlanen.this.startActivityForResult(newAct, 10);
                        }
                    }
                }
            }
        });

        berechnen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dataList.size()>=2){
                    XMLObject xml = new XMLObject();
                    for(int i=0; i<dataList.size(); i++) {
                        Log.d("DEBUG1", "currently at element: " + i);
                        xml.addElement(dataList.get(i));
                        xml.addElement("0.0");
                        xml.addElement("WeightTime");
                    }

                    //xml.addElement("0.0");
                    //xml.addElement("WeightTime");
                    Log.d("DEBUG1", "RoutePlanenErgebnisse starten");
                    Intent myIntent = new Intent(RoutePlanen.this, RoutePlanenErgebnisse.class);
                    data = xml;
                    RoutePlanen.this.startActivity(myIntent);
                } else {
                    Toast.makeText(act, "Ungültige Eingabe", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static XMLObject getData() {
        return data;
    }

    @Override
    public void onResume(){
        super.onResume();

        if(dataList==null){
            dataList = new ArrayList<String>();
        } else {
            if(!dataList.isEmpty()){
                loadData();
            }
        }
    }

    public static void setDataList(ArrayList<String> list) {
        dataList = list;
    }

    private static void loadData(){
        start.setText(dataList.get(0));
        mid.setText("");
        for(int i=1; i<dataList.size()-1; i++){
            if(mid.getText()=="") {
                mid.setText(dataList.get(i) + "; ");
            } else {
                mid.setText(mid.getText() + dataList.get(i) + "; ");
            }
        }
        end.setText(dataList.get(dataList.size()-1));

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS ist deaktiviert. Wollen Sie es aktivieren?")
                .setCancelable(false)
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        Intent newAct = new Intent(RoutePlanen.this, StandortAuswahl.class);
                        RoutePlanen.this.startActivity(newAct);
                    }
                })
                .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    Intent newAct = new Intent(RoutePlanen.this, StandortAuswahl.class);
                    RoutePlanen.this.startActivity(newAct);
                } else {
                    // permission denied, boo!.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
