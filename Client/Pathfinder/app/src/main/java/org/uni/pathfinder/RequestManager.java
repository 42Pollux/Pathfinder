package org.uni.pathfinder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

    public static void requestPath(XMLObject xml ) {

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
    public byte type;
    public long item_id;
    public View view;
    public Activity act;
    public byte map_code;
    public double[] sector;

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

    public Request(Activity _act, byte _type, double[] _sector, View _view) {
        this.type = _type;
        this.sector = _sector;
        this.view = _view;
        this.act = _act;
    }
}


class Queue implements Runnable {
    public static volatile int queue_counter = 0;
    HashMap<Integer, Request> queue;
    // helper variables only to avoid 'is accessed from within inner class' compilation error
    private Bitmap helper_bmp;
    private ImageView helper_imgv;
    private String helper_txt;
    private TextView helper_txtv;
    private TileRendererLayer helper_trlayer;
    private MapView helper_mapv;
    private MapViewInitializer helper_mapvinit;


    public Queue(final HashMap<Integer, Request> queue_view){
        queue_counter = 0;
        queue = queue_view;
        int i = 0;

        RequestManager.connector.setOnNetworkingFinished(new NetworkingThreadFinishListener() {
            @Override
            public void onNetworkingResult(int request_id, String message, Object data, boolean failure) {
                Queue.queue_counter = Queue.queue_counter--;
                Request r = queue.get(request_id);
                Log.d("DEBUG1", "HELLU");
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
                            r.act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    helper_mapv.getLayerManager().getLayers().add(helper_trlayer);
                                    helper_mapv.setCenter(new LatLong(helper_mapvinit.getLatitude(), helper_mapvinit.getLongitude()));
                                    helper_mapv.setZoomLevel((byte)helper_mapvinit.getZoomLevel());
                                }
                            });
                        }

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
