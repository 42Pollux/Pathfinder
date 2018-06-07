package org.uni.pathfinder;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.uni.pathfinder.activities.Hauptmenu;
import org.uni.pathfinder.activities.StandortAuswahl;
import org.uni.pathfinder.network.ConnectionCodes;
import org.uni.pathfinder.network.Connector;
import org.uni.pathfinder.network.MapViewInitializer;
import org.uni.pathfinder.network.NetworkingConsoleListener;
import org.uni.pathfinder.network.NetworkingThreadFinishListener;
import org.uni.pathfinder.shared.XMLObject;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import org.mapsforge.map.view.MapView;
import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.uni.pathfinder.network.ConnectionCodes.HISTORY;
import static org.uni.pathfinder.network.ConnectionCodes.PATH;
import static org.uni.pathfinder.network.ConnectionCodes.REGISTER;
import static org.uni.pathfinder.network.ConnectionCodes.SECTOR;

public class RequestManager {
    private static Thread q;
    private static HashMap<Integer, Request> queue_view = new HashMap<Integer, Request>();
    private static int id_counter = 0;
    private static Activity logger_act;
    private static TextView logger_txtv;
    public static Context appContext;
    public static Connector connector;


    public static void initialize(Context _context) {
        appContext = _context;
        Connector con = new Connector(_context);
        connector = con;
        createDirectories();
    }

    public static boolean isOnline(Context _context) {
        ConnectivityManager cm =
                (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void requestUUID(Activity act) {
        Request r = new Request(act, appContext, ConnectionCodes.REGISTER);
        queue_view.put(id_counter, r);
        id_counter++;
    }

    public static void requestPath(Activity act, XMLObject xml, View view) {
        Request r = new Request(act, ConnectionCodes.PATH, xml, view);
        queue_view.put(id_counter, r);
        id_counter++;
    }

    public static void requestHistory(Activity act, View view, View view2) {
        Request r = new Request(act, ConnectionCodes.HISTORY, view, view2, appContext);
        queue_view.put(id_counter, r);
        id_counter++;
    }

    public static void requestMap(Activity act, byte map_code) {
        Request r = new Request(act, ConnectionCodes.MAP, map_code);
        queue_view.put(id_counter, r);
        id_counter++;
    }

    public static void requestSector(Activity act, double[] sector, View view) {
        Request r = new Request(act, SECTOR, sector, view);
        queue_view.put(id_counter, r);
        id_counter++;
    }

    public static void requestImage(Activity act, long image_id, View view) {
        Request r = new Request(act, ConnectionCodes.IMAGE, image_id, view);
        queue_view.put(id_counter, r);
        id_counter++;
    }

    public static void requestText(Activity act, long text_id, View view) {
        Request r = new Request(act, ConnectionCodes.TEXT, text_id, view);
        queue_view.put(id_counter, r);
        id_counter++;
    }

    public static void flush() {
        q = new Thread(new Queue(queue_view));
        q.start();
    }

    public static void setDebugConsole(Activity act, TextView txtv) {
        logger_act = act;
        logger_txtv = txtv;
        connector.setOnNetworkingProgress(new NetworkingConsoleListener() {
            @Override
            public void onNetworkingProgress(final String message) {
                logger_act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logger_txtv.setText(logger_txtv.getText() + "\n" + message);
                    }
                });
            }
        });
    }

    private static void createDirectories(){
        String res_dir = appContext.getFilesDir().getAbsolutePath() + "/res";
        String map_dir = appContext.getFilesDir().getAbsolutePath() + "/map";
        String tmp_dir = appContext.getFilesDir().getAbsolutePath() + "/tmp";

        File dir = new File(res_dir);
        dir.mkdir();
        dir = new File(map_dir);
        dir.mkdir();
        dir = new File(tmp_dir);
        dir.mkdir();
    }
}

class Request {
    public Context context;
    public byte type;
    public long item_id;
    public View view;
    public View view2;
    public Activity act;
    public byte map_code;
    public double[] sector;
    public XMLObject xml;

    public Request(Activity _act, byte _type, long _item_id, View _view) {
        this.type = _type;
        this.item_id = _item_id;
        this.view = _view;
        this.act = _act;
    }

    public Request(Activity _act, byte _type, byte _code) {
        this.type = _type;
        this.map_code = _code;
        this.act = _act;
    }

    public Request(Activity _act, byte _type, View _view, View _view2, Context _context) {
        this.type = _type;
        this.view = _view;
        this.view2 = _view2;
        this.act = _act;
        this.context = _context;
    }

    public Request(Activity _act, byte _type, double[] _sector, View _view) {
        this.type = _type;
        this.sector = _sector;
        this.view = _view;
        this.act = _act;
    }

    public Request(Activity _act, Context _context, byte _type){
        this.context = _context;
        this.type = _type;
        this.act = _act;
    }

    public Request(Activity _act, byte _type, XMLObject _xml, View _view){
        this.view = _view;
        this.xml = _xml;
        this.type = _type;
        this.act = _act;
    }
}


class Queue implements Runnable {
    public static volatile int queue_counter = 0;
    HashMap<Integer, Request> queue;
    // helper variables only to avoid 'is accessed from within inner class' compilation error
    private Context helper_context;
    private Bitmap helper_bmp;
    private ImageView helper_imgv;
    private String helper_txt;
    private TextView helper_txtv;
    private TileRendererLayer helper_trlayer;
    private MapView helper_mapv;
    private MapViewInitializer helper_mapvinit;
    private Activity helper_act;
    private XMLObject helper_xml;
    private ListView helper_listv;
    private ArrayList<ListViewEntry> helper_listventry;
    private RelativeLayout helper_relativel;


    public Queue(final HashMap<Integer, Request> queue_view){
        queue_counter = 0;
        queue = queue_view;
        int i = 0;

        RequestManager.connector.setOnNetworkingFinished(new NetworkingThreadFinishListener() {
            @Override
            public void onNetworkingResult(int request_id, String message, Object data, boolean failure) {
                Queue.queue_counter = Queue.queue_counter--;
                Request r = queue.get(request_id);
                //Log.d("DEBUG1", "HELLU");
                switch(r.type){
                    case ConnectionCodes.IMAGE:
                        if(!failure){
                            helper_bmp = BitmapFactory.decodeFile(message);
                            ImageView img = (ImageView) r.view;
                            helper_imgv = img;
                            r.act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    helper_imgv.setImageBitmap(helper_bmp);
                                }
                            });
                            } else {
                                // TODO put placeholder here? e.g. "no image found"-image
                        }
                        break;
                    case ConnectionCodes.TEXT:
                        if(!failure){
                            helper_txtv = (TextView) r.view;
                            helper_txt = message;
                            r.act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    helper_txtv.setText(helper_txt);
                                }
                            });
                        } else {
                            // TODO put placeholder here?
                        }
                        break;
                    case SECTOR:
                        if(!failure){
                            Log.d("DEBUG1", "Hi" + r.sector[0]);
                            helper_mapv = (org.mapsforge.map.android.view.MapView) r.view;
                            TileCache tile_cache = AndroidUtil.createTileCache(r.act, "mapcache", helper_mapv.getModel().displayModel.getTileSize(), 1f,
                                    helper_mapv.getModel().frameBufferModel.getOverdrawFactor());
                            File map_file = new File(message);
                            MapDataStore map_data = new MapFile(map_file);
                            helper_trlayer = new TileRendererLayer(tile_cache, map_data, helper_mapv.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);
                            helper_trlayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT);
                            helper_mapvinit = (MapViewInitializer) data;
                            Log.d("DEBUG1", "MapViewInitializer data: " + helper_mapvinit.getLatitude() + ", " + helper_mapvinit.getLongitude());
                            r.act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    helper_mapv.getLayerManager().getLayers().add(helper_trlayer);
                                    helper_mapv.setCenter(new LatLong(helper_mapvinit.getLatitude(), helper_mapvinit.getLongitude()));
                                    helper_mapv.setZoomLevel((byte)helper_mapvinit.getZoomLevel());
                                    StandortAuswahl.getLoader().setVisibility(View.GONE);
                                    StandortAuswahl.getCross().setVisibility(View.VISIBLE);
                                }
                            });
                        }
                        break;
                    case REGISTER:
                        if(!failure){
                            helper_act = r.act;
                            SharedPreferences spref = r.context.getSharedPreferences(Hauptmenu.SHAREDPREFKEY, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = spref.edit();
                            editor.putString("key_uuid", message);
                            editor.commit();
                            Log.d("DEBUG1", "HIERHIERHIERHIER " + message);

                            r.act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(helper_act, "Erfolgreich registriert!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            helper_act = r.act;
                            r.act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(helper_act, "Registrierung fehlgeschlagen!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
                    case PATH:
                        if(!failure) {
                            helper_xml = (XMLObject) data;
                            helper_txtv = (TextView) r.view;
                            r.act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String str = "";
                                    for(int i=0; i<helper_xml.getDataList().size()/2; i++) {
                                        str = str + "\n" + helper_xml.getDataList().get(i);
                                    }
                                    helper_txtv.setText(str);
                                }
                            });
                        }
                        break;
                    case HISTORY:
                        if(!failure) {
                            helper_context = r.context;
                            helper_xml = (XMLObject) data;
                            helper_listv = (ListView) r.view;
                            helper_relativel = (RelativeLayout) r.view2;
                            helper_listventry = new ArrayList<>();
                            r.act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int counter = 1;
                                    for(int i=0; i<helper_xml.getDataList().size()/2; i++) {
                                        if(helper_xml.getDataList().get(i).equals("end")){
                                            Log.d("DEBUG1", "Value 1:" + helper_xml.getDataList().get(i-1));
                                            Log.d("DEBUG1", "Value 2:" + helper_xml.getDataList().get(i-2));
                                            helper_listventry.add(new ListViewEntry("Route " + counter, helper_xml.getDataList().get(i-1), String.format("%.1f", Double.parseDouble(helper_xml.getDataList().get(i-2)))));
                                            counter++;
                                        }

                                    }
                                    helper_relativel.setVisibility(View.GONE);
                                    ListViewAdapter adapter = new ListViewAdapter(helper_listventry, helper_context);
                                    helper_listv.setAdapter(adapter);
                                    helper_listv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                            ListViewEntry dataModel = helper_listventry.get(position);
                                            // do stuff
                                        }
                                    });
                                }
                            });
                        }
                        break;
                }
                queue.remove(request_id);
                // get data TODO implement
            }
        });

    }


    @Override
    public void run() {
        Log.d("DEBUG1", "started");
        for(Map.Entry<Integer, Request> e: queue.entrySet()){
            while(queue_counter<3){
                Log.d("DEBUG1", "switching");
                switch(e.getValue().type){
                    case ConnectionCodes.SECTOR:
                        RequestManager.connector.requestMapSector(e.getKey(), e.getValue().sector, false);
                        break;
                    case ConnectionCodes.REGISTER:
                        RequestManager.connector.requestRegister(e.getKey());
                        break;
                    case ConnectionCodes.PATH:
                        RequestManager.connector.requestPaths(e.getKey(), e.getValue().xml);
                        break;
                    case ConnectionCodes.HISTORY:
                        RequestManager.connector.requestHistory(e.getKey());
                        break;
                }
                break;
            }
            while(queue_counter>2){
                try {
                    Thread.sleep(1000);
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        }
    }



}
