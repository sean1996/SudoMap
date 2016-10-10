package com.anchronize.sudomap.navigationdrawer;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.anchronize.sudomap.R;
import com.anchronize.sudomap.SudoMapApplication;
import com.anchronize.sudomap.navigationdrawer.youreventfragments.BookmarkFragment;
import com.anchronize.sudomap.navigationdrawer.youreventfragments.AttendingFragment;
import com.anchronize.sudomap.objects.Event;
import com.anchronize.sudomap.objects.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//Glarence Zhao
//Tab UI code modified/referenced from Android Hive
//http://www.androidhive.info/2015/09/android-material-design-working-with-tabs/

public class YourEventActivity extends AppCompatActivity {

    private Firebase ref;

    private Toolbar toolBar;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;

    //FOR TESTING
    private ArrayList<Event> upcomingEvents = new ArrayList<Event>();       //attending
    private ArrayList<Event> pastEvents = new ArrayList<Event>();           //bookmarked
    public static final String UPCOMING_KEY = "UPCOMING EVENTS";
    public static final String PAST_KEY = "PAST EVENTS";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_event);

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set context for firebase
        Firebase.setAndroidContext(this);
        ref = new Firebase("https://anchronize.firebaseio.com");

        populateEvents();

        viewPager = (CustomViewPager) findViewById(R.id.container);
        viewPager.setPagingEnabled(true);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter viewPageAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        AttendingFragment upcomingFragment = new AttendingFragment();
        BookmarkFragment pastFragment = new BookmarkFragment();
        //setup Array passing
        Bundle upcomingBundle = new Bundle();
        upcomingBundle.putSerializable(UPCOMING_KEY, upcomingEvents);
        upcomingFragment.setArguments(upcomingBundle);
        Bundle pastBundle = new Bundle();
        pastBundle.putSerializable(PAST_KEY, pastEvents);
        pastFragment.setArguments(pastBundle);

        viewPageAdapter.addFragment(upcomingFragment, getResources().getString(R.string.attending_fragment_title));
        viewPageAdapter.addFragment(pastFragment, getResources().getString(R.string.bookmark_fragment_title));
        viewPager.setAdapter(viewPageAdapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<Fragment>();
        private final List<String> fragmentTitleList = new ArrayList<String>();

        public ViewPagerAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    //FOR TESTING
    private void populateEvents(){
        User currentUser = ((SudoMapApplication)getApplication()).getCurrentUser();
        for(String attendingEventID: currentUser.getAttendingEventIDs()){
            Firebase refEvent = ref.child("events").child(attendingEventID);
            refEvent.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Event attendingEvent = dataSnapshot.getValue(Event.class);
                    upcomingEvents.add(attendingEvent);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }

        for(String bookmarkedEventID: currentUser.getBookmarkedEventIDs()){
            Firebase refEvent = ref.child("events").child(bookmarkedEventID);
            refEvent.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Event bookmarkedEvent = dataSnapshot.getValue(Event.class);
                    pastEvents.add(bookmarkedEvent);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }

    }
}
