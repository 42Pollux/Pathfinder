package org.uni.pathfinder.activities;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Printer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.uni.pathfinder.ListViews.ListViewAdapter;
import org.uni.pathfinder.ListViews.ListViewEntry;
import org.uni.pathfinder.R;
import org.uni.pathfinder.RequestManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MeineRouten extends AppCompatActivity {
    private static ArrayList<ListViewEntry> saved_routes;
    private static RelativeLayout loader;

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

        ListView listView = findViewById(R.id.listViewMeineRouten);

        if(saved_routes!=null){
            ListViewAdapter adapter = new ListViewAdapter(saved_routes, getApplicationContext(), R.layout.list_element_meinerouten);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    ListViewEntry dataModel = saved_routes.get(position);
                    // do stuff
                }
            });
        } else {
        }

    }


    @Override
    public void onStart(){
        super.onStart();
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

    public static ArrayList<ListViewEntry> getSavedRoutes(){
        return saved_routes;
    }

    public static void setSavedRoutes(ArrayList<ListViewEntry> data, boolean init_flag) {
        saved_routes = data;
        if(!init_flag) {
            writeSavedRoutes(data);
        }
    }

    private static void writeSavedRoutes(ArrayList<ListViewEntry> data) {
        try {
            FileOutputStream out_f = new FileOutputStream(Hauptmenu.APPCONTEXT.getFilesDir().getAbsolutePath() + "/stored_routes.bin");
            PrintWriter print = new PrintWriter(out_f);
            int counter = 0;
            for(ListViewEntry entry : data) {
                for(String str : entry.getPath()) {
                    print.write(str + "\n");
                    counter++;
                }
            }
            print.close();
            out_f.close();
            Log.d("MeineRouten", "saved routes stored with " + counter + " lines");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ListViewEntry> readSavedRoutes() {
        try {
            FileInputStream in_f = new FileInputStream(Hauptmenu.APPCONTEXT.getFilesDir().getAbsolutePath() + "/stored_routes.bin");
            BufferedReader in = new BufferedReader(new InputStreamReader(in_f));
            String line = "";
            ArrayList<ListViewEntry> entries = new ArrayList<>();
            ArrayList<ArrayList<String>> ret = new ArrayList<>();
            ArrayList<String> list = new ArrayList<>();
            while((line = in.readLine())!=null) {
                list.add(line);
                if(line.equals("end")){
                    ret.add(list);
                    list = new ArrayList<>();
                }
            }
            int routen_counter = 1;
            for(ArrayList<String> tmp : ret){
                ListViewEntry ent = new ListViewEntry("Route "+routen_counter, tmp.get(tmp.size()-2), String.format("%.1f", Double.parseDouble(tmp.get(tmp.size()-3))), R.drawable.ic_heart_grey600_24dp);
                ent.setPath(tmp);
                routen_counter++;
                entries.add(ent);
            }
            in.close();
            in_f.close();
            if(!entries.isEmpty()) {
                return entries;
            } else {
                return null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
