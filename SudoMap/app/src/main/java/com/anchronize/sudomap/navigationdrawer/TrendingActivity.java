package com.anchronize.sudomap.navigationdrawer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.anchronize.sudomap.EventDetailActivity;
import com.anchronize.sudomap.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TrendingActivity extends AppCompatActivity {
    private ArrayList<String> events = new ArrayList<String>();
    private static final String FIREBASE_URL = "https://anchronize.firebaseio.com";
    private Map<String, Integer> map; //<eventID, number of posts so far>
    private Map<String, Integer> categoryMap; //<event category, number of event>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending);
        //set up Firebase reference for chat
        Firebase mRootRef = new Firebase(FIREBASE_URL);
        Firebase mChatRef = new Firebase(FIREBASE_URL).child("chat");
        Firebase mEventRef = new Firebase(FIREBASE_URL).child("events");
        //initialize the map
        //<eventID, number of posts so far>
        map = new HashMap<>();
        categoryMap = new HashMap<>();


        //query the server once
        mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot chatSnapshot = dataSnapshot.child("chat");
                DataSnapshot eventSnapshot = dataSnapshot.child("events");
                for (DataSnapshot chatInnerSnapshot : chatSnapshot.getChildren()) {
                    String eventID = chatInnerSnapshot.getKey();
                    Integer num = Integer.valueOf((int) chatInnerSnapshot.getChildrenCount());
                    map.put(eventID, num);
                }
                map = sortByValue(map);
                List<String> eventList = new ArrayList<String>();
                List<String> eventIDList = new ArrayList<String>();
                Log.d("EventsMap",map.toString());
                int j = 0;
                for (Map.Entry<String, Integer> entry : map.entrySet()) {
                    String id = entry.getKey();
                    Log.d("EventID2", id);
                    String eventTitle = (String) eventSnapshot.child(id).child("title").getValue();
                    //have to concatenate two strings since adapter accepts only one
                    //list of strings
                    if(eventTitle == null){
                        Log.d("NullPointerError", "Error");
                    }
                    //String eventString = eventTitle.concat("*" + id); //event title first, then eventID
                    String eventString = eventTitle + "*" + id;
                    //Log.d("EventTitle",eventTitle);
                    Log.d("EventString", eventString);
                    eventList.add(eventString);
                    j++;
                    Log.d("EventCounter", Integer.toString(j));
                }

                //calculating category percentage
                for(DataSnapshot temp : eventSnapshot.getChildren()){
                    String category = (String) temp.child("category").getValue();
                    Integer count = categoryMap.get(category);
                    if(count ==  null){
                        categoryMap.put(category, 1);
                    }
                    else{
                        categoryMap.put(category, count + 1);
                    }
                }

                int totalCount = (int) eventSnapshot.getChildrenCount();

                Log.d("eventList", eventList.toString());
                MyListAdaper la = new MyListAdaper(getApplicationContext(), R.layout.event_list_item, eventList);
                final ListView trendingListView = (ListView) findViewById(R.id.mylist);
                trendingListView.setAdapter(la);
                trendingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String eventID = (String) trendingListView.getItemAtPosition(position);
                        String[] split = eventID.split("\\*");
                        eventID = split[1];
                        Log.d("eventID", eventID);
                        //Toast.makeText(TrendingActivity.this, "List item was clicked at " + eventID, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), EventDetailActivity.class);
                        i.putExtra(EventDetailActivity.EVENTID_KEY, eventID);
                        startActivity(i);
                    }
                });

                PieChart pieChart = (PieChart) findViewById(R.id.chart);

                // creating list of entry
                ArrayList<Entry> entries = new ArrayList<>();
                ArrayList<String> labels = new ArrayList<String>();

                Log.d("category", categoryMap.toString());

                int i = 0;
                //calculate percentage and set the piechart
                for (Map.Entry<String, Integer> entry : categoryMap.entrySet()) {
                    String str = entry.getKey();
                    int num = entry.getValue();
                    float percent = num * 100.0f/(totalCount);
                    entries.add(new Entry(percent, i));
                    labels.add(str);
                    i++;

                }





                PieDataSet dataset = new PieDataSet(entries, "# of Events");
                dataset.setColors(ColorTemplate.COLORFUL_COLORS);


                dataset.setValueTextSize(13);
                pieChart.setDescriptionTextSize(13);

                PieData data = new PieData(labels, dataset);
                pieChart.setData(data); // set the data and list of lables into chart
                pieChart.setDescription("Events by category %");  // set the description
                pieChart.animateY(2000);


            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }


    private class MyListAdaper extends ArrayAdapter<String> {
        private int layout;
        private List<String> mObjects;

        private MyListAdaper(Context context, int resource, List<String> objects) {
            super(context, resource, objects);

            mObjects = objects;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewholder = null;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.list_item_title);
                viewHolder.button = (ImageButton) convertView.findViewById(R.id.list_item_btn_right);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();
            mainViewholder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getContext(), "Button was clicked for list item " + position, Toast.LENGTH_SHORT).show();
                }
            });
            String str = getItem((position));
            String[] split = str.split("\\*");
            String eventName = split[0];
            mainViewholder.title.setText(eventName);
            return convertView;
        }
    }

    public class ViewHolder {

        TextView title;
        ImageButton button;
    }


    //following implementation of sorting by map value is taken from
    //http://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java
    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list =
                new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o2, Map.Entry<K, V> o1) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
