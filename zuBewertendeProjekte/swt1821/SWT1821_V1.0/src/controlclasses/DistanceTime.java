package controlclasses;

import java.io.IOException;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import okhttp3.*;


@Component
public class DistanceTime {



    private static final String API_KEY="AIzaSyB1JRafw5GwnTxXt_BhCZRvSAEN9Yve0wI"; //YOUR KEY
    OkHttpClient client = new OkHttpClient();


    /*
     * Methode bei der Start, Ziel und Vehikel eingegeben werden
     * und ein String in der Form eines JSON ausgeben wird, der
     * die Reisezeit in sekunden enthält.
     */
    public String calculate(String source ,String destination, String mode) throws IOException {
    		String url="https://maps.googleapis.com/maps/api/distancematrix/json?origins="+source+"&destinations="+destination+"&mode="+mode+"&key="+ API_KEY;
            Request request = new Request.Builder()
                .url(url)
                .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
    }
    
    public int getZeit(String source ,String destination, String mode) throws IOException {
    	int zeit;
    	String str=calculate(source, destination, mode);
    	/*
    	 * String wird zu einem JSON geparsed, aus dem dann die Zeit
    	 * als int ausgelesen und zurückgegeben wird
    	 */
    	JSONObject obj = new JSONObject(str);
    	zeit=obj.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getInt("value");
    	return zeit;
    }
}