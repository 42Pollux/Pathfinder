package org.uni.pathfinder.ListViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.uni.pathfinder.R;

import java.util.ArrayList;

public class ListViewAdapterMeineRouten extends ArrayAdapter<ListViewEntryMeineRouten> implements View.OnClickListener{

    private ArrayList<ListViewEntryMeineRouten> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtHeigthDistance;
        ImageView info;
    }

    public ListViewAdapterMeineRouten(ArrayList<ListViewEntryMeineRouten> data, Context context) {
        super(context, R.layout.list_element_verlauf, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object= getItem(position);
        ListViewEntryMeineRouten dataModel = (ListViewEntryMeineRouten) object;

        switch (v.getId())
        {
            case R.id.favourite:
                // TODO favourite
                ImageView imgv = (ImageView) v;
                imgv.setBackgroundResource(R.drawable.ic_heart_grey600_24dp);

                break;
        }
    }

    private int lastPosition = -1;

    public ArrayList<ListViewEntryMeineRouten> getDataSet() {
        return dataSet;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ListViewEntryMeineRouten dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_element_verlauf, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.firstLine);
            viewHolder.txtHeigthDistance = (TextView) convertView.findViewById(R.id.secondLine);

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
        viewHolder.txtHeigthDistance.setText(dataModel.getDatum() + ", " + dataModel.getDistance() + "km");
        // Return the completed view to render on screen
        return convertView;
    }
}