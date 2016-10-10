package com.anchronize.sudomap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anchronize.sudomap.objects.Event;
import com.anchronize.sudomap.objects.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity implements

        ActivityCompat.OnRequestPermissionsResultCallback{
    //A unique keyname, so that mainActivity can use this key and pass selected event to this activity
    public static final String EVENT_KEY = "com.anchronize.sudomap.EventDetailActivity.eventKEY";
    public static final String EVENTID_KEY = "com.anchronize.sudomap.EventDetailActivity.eventIDKEY";

    private TextView titleView;
    private TextView organizerView;
    private TextView locationNameView;
    private TextView locationAddress;
    private TextView descriptionView;
    private TextView categoryView;
    private TextView startDateTextView;
    private HorizontalScrollView attendantsScrollView;
    private LinearLayout attendantsView;
    private Button chatButton;
    private Button bookmarkButton;
    private Button attendingButton;

    private Event mEvent;
    private String mEventID;

    private Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail_updated);
        ref = new Firebase("https://anchronize.firebaseio.com");
        initializeComponents();
        addListeners();
    }

    public void initializeComponents(){
//        //Map
//        SupportMapFragment mapFragment =
//                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.eventsMap);
//        mapFragment.getMapAsync(this);

        //GUI
        titleView = (TextView) findViewById(R.id.eventTitle);
        categoryView = (TextView)findViewById(R.id.categoryTextView);
        organizerView = (TextView) findViewById(R.id.organizerInfo);
        locationNameView = (TextView) findViewById(R.id.eventLocationNameTextView);
        locationAddress = (TextView) findViewById(R.id.eventLocationAddressTextView);
        descriptionView = (TextView) findViewById(R.id.eventDescriptionView);
        attendantsScrollView = (HorizontalScrollView) findViewById(R.id.attendantsScrollView);
        startDateTextView = (TextView)findViewById(R.id.startDateTimeTextView);
        attendantsView = (LinearLayout) findViewById(R.id.attendants);
        chatButton = (Button) findViewById(R.id.chat_button);
        bookmarkButton = (Button) findViewById(R.id.bookmarkButton);
        attendingButton = (Button) findViewById(R.id.attendingButton);

        //Current Event
        Intent i = getIntent();
        if(i.hasExtra(EVENTID_KEY)){
            mEventID = i.getStringExtra(EVENTID_KEY);
            Firebase refEventID = ref.child("events").child(mEventID);
            refEventID.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mEvent = dataSnapshot.getValue(Event.class);
                    populateDetails();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }
        else{
            mEvent = (Event)i.getSerializableExtra(EVENT_KEY);
            populateDetails();
        }
    }

    public void addListeners(){
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatButtonClicked();
            }
        });
        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookmarkButtonClicked();
            }
        });
        attendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attendingButtonClicked();
            }
        });
    }

    public void chatButtonClicked(){
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra(ChatActivity.EVENTID_KEY,mEvent.getEventID());
        i.putExtra(ChatActivity.EVENTDESC_KEY, mEvent.getDescription());
        i.putExtra(ChatActivity.USERNAME_KEY, ((SudoMapApplication) getApplication()).getCurrentUser().getInAppName());
        startActivity(i);
    }

    public void bookmarkButtonClicked(){
        //get current user and update bookmarked events
        User user = ((SudoMapApplication)getApplication()).getCurrentUser();
        if(user.getBookmarkedEventIDs().contains(mEvent.getEventID())){
            return;         //if user already bookmark this event, do nothing
        }
        user.addBookmarkedEventID(mEvent.getEventID());

        //update the firebase
        Firebase refUser = ref.child("users").child(user.getUserID());
        Map<String, Object> eventBookmarked = new HashMap<String, Object>();
        eventBookmarked.put("bookmarkedEventIDs", user.getBookmarkedEventIDs());
        refUser.updateChildren(eventBookmarked);
        finish();
    }

    public void attendingButtonClicked(){
        //get current user and update attending events
        User user = ((SudoMapApplication)getApplication()).getCurrentUser();
        if(user.getAttendingEventIDs().contains(mEvent.getEventID())){
            return;         //if user already attend this event, do nothing
        }
        user.addAttendingEventID(mEvent.getEventID());


        //update the firebase - user part
        Firebase refUser = ref.child("users").child(user.getUserID());
        Map<String, Object> eventAttending = new HashMap<String, Object>();
        eventAttending.put("attendingEventIDs", user.getAttendingEventIDs());
        refUser.updateChildren(eventAttending);

        //update the firebases - event part
        Firebase refEvent = ref.child("events").child(mEvent.getEventID());
        ArrayList<String> attendentIDs = mEvent.getattendantsID();
        if(!attendentIDs.contains(user.getUserID())){
            //update the attendentIDs array
            attendentIDs.add(user.getUserID());
            Map<String, Object> attendee = new HashMap<String, Object>();
            attendee.put("attendantsID", attendentIDs);
            refEvent.updateChildren(attendee);
        }
        finish();
    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        mMap.addMarker(new MarkerOptions().position(new
//                LatLng(mEvent.getLatitude(), mEvent.getLongitude())).title("Hello world"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mEvent.getLatitude(), mEvent.getLongitude()), 15));
//    }

    public void populateDetails(){

        //get Organizer from Firebase, set organizer's name to textView
        Firebase refOrganizer = ref.child("users").child(mEvent.getOrganizerID());
        refOrganizer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User organizer = dataSnapshot.getValue(User.class);
                //update the organizer text view here
                organizerView.setText(organizer.getInAppName());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        titleView.setText(mEvent.getTitle());
        categoryView.setText(mEvent.getCategory() + "     |  ");
        locationNameView.setText(mEvent.getAddressName());
        locationAddress.setText(mEvent.getAddress());
        descriptionView.setText(mEvent.getDescription());
        startDateTextView.setText(mEvent.formattedDateString());

        for(String userID: mEvent.getattendantsID()){
            Firebase attendeeRef = ref.child("users").child(userID);
            attendeeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User attendee = dataSnapshot.getValue(User.class);

                    AttendantsItem item = new AttendantsItem(getApplicationContext());
                    item.setName(attendee.getInAppName());
                    item.setPicBitMap(attendee.getProfileImageBitMap());
                    attendantsView.addView(item);

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }
    }

    public String addressFromLatLng(double lat, double lng){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "RETRIEVE ADDRESS FAILED";
    }

    public String nameFromLatLng(double lat, double lng){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0).getFeatureName(); // Only if available else return NULL
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "RETRIEVE ADDRESS NAME FAILED";
    }


}
