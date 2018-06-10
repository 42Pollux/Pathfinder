package org.uni.pathfinder.ListViews;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.uni.pathfinder.R;
import org.uni.pathfinder.activities.MeineRouten;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ListViewAdapter extends ArrayAdapter<ListViewEntry> implements View.OnClickListener{

    private ArrayList<ListViewEntry> dataSet;
    private int layout;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtHeigthDistance;
        ImageView image;
    }

    public ListViewAdapter(ArrayList<ListViewEntry> data, Context context, int _layout) {
        super(context, _layout, data);
        this.layout = _layout;
        this.dataSet = data;
        this.mContext = context;

        for(int i=0; i<data.size(); i++){
            data.get(i).setImage(R.drawable.ic_heart_outline_grey600_24dp);
        }
        if(MeineRouten.getSavedRoutes()!=null)  {
            for(int i=0; i<data.size(); i++){
                for(int j=0; j<MeineRouten.getSavedRoutes().size(); j++) {
                    if(comparePaths(data.get(i).getPath(), MeineRouten.getSavedRoutes().get(j).getPath())==true){
                        data.get(i).setImage(R.drawable.ic_heart_grey600_24dp);
                    }
                }
            }
        }

        for(int i=0; i<data.size(); i++){
            //Log.d("ListViewAdapter/<init>", "Image: " + data.get(i).getImage());
            for(String s : data.get(i).getPath()) {
                Log.d("ListViewAdapter/<init>", "Content: " + s);
            }
        }
        Log.d("ListViewAdapter/<init>", "---------------------------");
        if(MeineRouten.getSavedRoutes()==null) return;
        for(int i=0; i<MeineRouten.getSavedRoutes().size(); i++){
            //Log.d("ListViewAdapter/<init>", "Image: " + data.get(i).getImage());
            for(String s : MeineRouten.getSavedRoutes().get(i).getPath()) {
                Log.d("ListViewAdapter/<init>", "Content: " + s);
            }
        }
    }

    private boolean comparePaths(ArrayList<String> a, ArrayList<String> b){
        if(a.size()!=b.size()) return false;

        // length comparison not reliable, but current work around
        double a_length = Double.parseDouble(a.get(a.size()-3));
        double b_length = Double.parseDouble(b.get(b.size()-3));
        if(a_length==b_length) return true;

        for(int i=0; i<a.size(); i++) {
            if(!a.get(i).equals(b.get(i))) return false;
        }
        Log.d("ListViewAdapter/comp", "Found favourite");
        return true;
    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        ListViewEntry dataModel = (ListViewEntry) object;

        ImageView imgv;
        switch (v.getId())
        {
            case R.id.verlauf_favourite:
                imgv = (ImageView) v;

                if(dataModel.getImage() == R.drawable.ic_heart_grey600_24dp) {
                    imgv.setImageResource(R.drawable.ic_heart_outline_grey600_24dp);
                    dataModel.setImage(R.drawable.ic_heart_outline_grey600_24dp);
                    if(MeineRouten.getSavedRoutes()!=null) {
                        ArrayList<ListViewEntry> entries = MeineRouten.getSavedRoutes();
                        entries.remove(dataModel);
                        MeineRouten.setSavedRoutes(entries, false);
                    }
                    Log.d("ListViewAdapter/onClick", "Favorit entfernt");
                    Toast.makeText(mContext, "Von Favoriten entfernt!", Toast.LENGTH_SHORT).show();
                } else {
                    imgv.setImageResource(R.drawable.ic_heart_grey600_24dp);
                    dataModel.setImage(R.drawable.ic_heart_grey600_24dp);
                    if(MeineRouten.getSavedRoutes()!=null) {
                        ArrayList<ListViewEntry> ret = MeineRouten.getSavedRoutes();
                        ret.add(dataModel);
                        MeineRouten.setSavedRoutes(ret, false);

                    } else {
                        ArrayList<ListViewEntry> ret = new ArrayList<>();
                        ret.add(dataModel);
                        MeineRouten.setSavedRoutes(ret, false);

                    }
                    Log.d("ListViewAdapter/onClick", "Favorit hinzugefügt");
                    Toast.makeText(mContext, "Zu Favoriten hinzugefügt!", Toast.LENGTH_SHORT).show();
                }

                break;


            case R.id.meinerouten_favourite:
                imgv = (ImageView) v;

                ArrayList<ListViewEntry> entries = MeineRouten.getSavedRoutes();
                entries.remove(dataModel);
                MeineRouten.setSavedRoutes(entries, false);
                this.remove(dataModel);
                this.notifyDataSetChanged();
        }
    }

    private int lastPosition = -1;

    public ArrayList<ListViewEntry> getDataSet() {
        return dataSet;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ListViewEntry dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.firstLine);
            viewHolder.txtHeigthDistance = (TextView) convertView.findViewById(R.id.secondLine);
            if(layout==R.layout.list_element_verlauf) {
                viewHolder.image = (ImageView) convertView.findViewById(R.id.verlauf_favourite);
            } else if(layout==R.layout.list_element_meinerouten) {
                viewHolder.image = (ImageView) convertView.findViewById(R.id.meinerouten_favourite);
            }

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(dataModel.getName());
        if(dataModel.getDatum()!=null) {
            viewHolder.txtHeigthDistance.setText(getDateGerman(dataModel.getDatum()) + ",  " + dataModel.getDistance() + "km");
        } else {
            viewHolder.txtHeigthDistance.setText( dataModel.getDistance() + "km");
        }
        if(layout!=R.layout.list_element_ergebnisse) {
            if(dataModel.getImage()==R.drawable.ic_heart_grey600_24dp) {
                viewHolder.image.setImageResource(R.drawable.ic_heart_grey600_24dp);
            } else {
                viewHolder.image.setImageResource(R.drawable.ic_heart_outline_grey600_24dp);
            }
            viewHolder.image.setOnClickListener(this);
            viewHolder.image.setTag(position);
        }

        // Return the completed view to render on screen
        return convertView;
    }

    private String getDateGerman(String datestring){
        // 2018-05-06 17:30:45
        String[] tmp = datestring.split(" ");
        String[] dates = tmp[0].split("-");
        String[] times = tmp[1].split(":");

        int year = Integer.parseInt(dates[0]);
        int month = Integer.parseInt(dates[1]);
        int day = Integer.parseInt(dates[2]);

        return day + ". " + getMonthGerman(month) + " " + year + " um " + times[0] + ":" + times[1] + " Uhr";
    }

    private String getMonthGerman(int month){
        switch(month) {
            case 1: return "Januar";
            case 2: return "Februar";
            case 3: return "März";
            case 4: return "April";
            case 5: return "Mai";
            case 6: return "Juni";
            case 7: return "Juli";
            case 8: return "August";
            case 9: return "September";
            case 10: return "Oktober";
            case 11: return "November";
            case 12: return "Dezember";

        }
        return "Monat";
    }
}