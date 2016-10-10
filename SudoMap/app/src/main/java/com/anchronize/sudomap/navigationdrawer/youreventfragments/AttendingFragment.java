package com.anchronize.sudomap.navigationdrawer.youreventfragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.anchronize.sudomap.EventDetailActivity;
import com.anchronize.sudomap.R;
import com.anchronize.sudomap.navigationdrawer.TabAdapter;
import com.anchronize.sudomap.objects.Event;

import java.util.ArrayList;

/**
 * Created by glarencezhao on 4/14/16.
 */
public class AttendingFragment extends Fragment {

    private ArrayList<Event> _upcomingEvents;
    public static final String UPCOMING_KEY = "UPCOMING EVENTS";

    private TabAdapter upcomingAdapter;

    private ListView upcomingEventsLV;

    public static final String EVENT_KEY = "com.anchronize.sudomap.EventDetailActivity.event";

    public AttendingFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        upcomingEventsLV = (ListView) view.findViewById(R.id.pastEventsListView);

        Bundle pastBundle = getArguments();
        if(pastBundle != null){
            _upcomingEvents = (ArrayList<Event>) pastBundle.get(UPCOMING_KEY);
        }

        upcomingAdapter = new TabAdapter(getActivity(), _upcomingEvents);
        upcomingEventsLV.setAdapter(upcomingAdapter);

        listeners();

        return view;
    }

    private void listeners(){
        upcomingEventsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = _upcomingEvents.get(position);
                Intent i = new Intent(getActivity().getApplicationContext(), EventDetailActivity.class);
                i.putExtra(EventDetailActivity.EVENT_KEY, event);
                startActivity(i);
            }
        });
    }

}
