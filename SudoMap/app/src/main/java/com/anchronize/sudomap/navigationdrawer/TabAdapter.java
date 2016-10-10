package com.anchronize.sudomap.navigationdrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.anchronize.sudomap.R;
import com.anchronize.sudomap.objects.Event;

import java.util.ArrayList;

/**
 * Created by glarencezhao on 4/14/16.
 */
public class TabAdapter extends ArrayAdapter<Event> {

    private Context context;

    public TabAdapter(Context context, ArrayList<Event> eventArray){
        super(context, 0, eventArray);

        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Event event = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_yourevent_fragments, parent, false);
        }

        TextView eventTitleTV = (TextView) convertView.findViewById(R.id.eventTitleTextView);
        eventTitleTV.setText(event.getTitle());
        TextView eventDateTimeTV = (TextView) convertView.findViewById(R.id.dateTimeTextView);
        eventDateTimeTV.setText(event.formattedDateString());
        TextView locationTitleTV = (TextView) convertView.findViewById(R.id.locationTextView);
        locationTitleTV.setText(event.getAddressName());

        return convertView;
    }
}