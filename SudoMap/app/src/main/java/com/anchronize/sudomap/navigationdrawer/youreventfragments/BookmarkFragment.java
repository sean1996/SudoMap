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
public class BookmarkFragment extends Fragment {

    private ArrayList<Event> _pastEvents;
    public static final String PAST_KEY = "PAST EVENTS";

    private TabAdapter pastAdapter;

    private ListView pastEventsLV;

    public static final String EVENT_KEY = "com.anchronize.sudomap.EventDetailActivity.event";

    public BookmarkFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        pastEventsLV = (ListView) view.findViewById(R.id.pastEventsListView);

        Bundle pastBundle = getArguments();
        if(pastBundle != null){
            _pastEvents = (ArrayList<Event>) pastBundle.get(PAST_KEY);
        }

        pastAdapter = new TabAdapter(getActivity(), _pastEvents);
        pastEventsLV.setAdapter(pastAdapter);

        listeners();

        return view;
    }

    private void listeners(){
        pastEventsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = _pastEvents.get(position);
                Intent i = new Intent(getActivity().getApplicationContext(), EventDetailActivity.class);
                System.out.println(event.getTitle());
                i.putExtra(EventDetailActivity.EVENT_KEY, event);
                startActivity(i);
            }
        });
    }

}
