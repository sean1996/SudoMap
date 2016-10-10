package com.anchronize.sudomap;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.internal.ScrimInsetsFrameLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anchronize.sudomap.navigationdrawer.AddEventActivity;
import com.anchronize.sudomap.navigationdrawer.SettingActivity;
import com.anchronize.sudomap.navigationdrawer.TrendingActivity;
import com.anchronize.sudomap.navigationdrawer.YourEventActivity;
import com.anchronize.sudomap.objects.Event;
import com.anchronize.sudomap.objects.ShakeDetector;
import com.anchronize.sudomap.objects.User;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.fasterxml.jackson.databind.JsonNode;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hound.android.fd.HoundSearchResult;
import com.hound.android.fd.Houndify;
import com.hound.android.fd.HoundifyButton;
import com.hound.android.libphs.PhraseSpotterReader;
import com.hound.android.sdk.VoiceSearchInfo;
import com.hound.android.sdk.audio.SimpleAudioByteStreamSource;
import com.hound.core.model.sdk.CommandResult;
import com.hound.core.model.sdk.HoundResponse;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.liveo.adapter.NavigationLiveoAdapter;
import br.liveo.interfaces.OnItemClickListener;
import br.liveo.interfaces.OnPrepareOptionsMenuLiveo;
import br.liveo.model.HelpItem;
import br.liveo.model.HelpLiveo;
import br.liveo.navigationliveo.NavigationLiveoList;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

/**
 * Created by jasonlin on 4/17/16.
 */
public class HomeActivity extends NavigationLiveo implements OnItemClickListener {

    // Navdrawer (creds to https://github.com/rudsonlive/NavigationDrawer-MaterialDesign)
    private HelpLiveo mHelpLiveo;
    private OnPrepareOptionsMenuLiveo onPrepare = new OnPrepareOptionsMenuLiveo() {
        @Override
        public void onPrepareOptionsMenu(Menu menu, int position, boolean visible) {
        }
    };
    private View.OnClickListener onClickEvents = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            startActivity(new Intent(getApplicationContext(), TrendingActivity.class));
            closeDrawer();
        }
    };
    private View.OnClickListener onClickFooter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            closeDrawer();
            Log.d("nav", "5th element (exit app) clicked");
            if ( ((SudoMapApplication) getApplication()).getAuthenticateStatus()) {
                ((SudoMapApplication)getApplication()).setAuthenticateStatus(false);
                ((SudoMapApplication)getApplication()).setCurrentUserID(null);
                ((SudoMapApplication)getApplication()).setCurrentUser(null);
//                finish();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                HomeActivity.this.finish();

            }
            else {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                HomeActivity.this.finish();
            }
            closeDrawer();
        }
    };
    private FloatingSearchView mSearchView;
    private FragmentTransaction ft;
    private HoundifyButton hb;


    public void populateNavDrawerInfo(){


        User current = ((SudoMapApplication)getApplication()).getCurrentUser();
        if (((SudoMapApplication)getApplication()).getAuthenticateStatus()) {
            // User Information
//            String currentUserID_ = ((SudoMapApplication)getApplication()).getCurrentUserID();
//            Firebase refCurrentUserRef = ref.child("users").child(currentUserID_);
//            refCurrentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    User currentUser = dataSnapshot.getValue(User.class);
//                    //do whatever you want to do here
//                    HomeActivity.this.userName.setText(currentUser.getInAppName());
//                }
//
//                @Override
//                public void onCancelled(FirebaseError firebaseError) {
//
//                }
//            });
//            this.userName.setText(((SudoMapApplication) getApplication()).getCurrentUsername());
//            this.userEmail.setText("uscjlin@gmail.com");
//            this.userPhoto.setImageBitmap(current.getProfileImageBitMap());
            this.userBackground.setImageResource(R.drawable.ic_user_background_first);
        }
        else{
            this.userName.setText("Guest");
//            this.userEmail.setText("uscjlin@gmail.com");
//            this.userPhoto.setImageBitmap();
            this.userBackground.setImageResource(R.drawable.ic_user_background_first);
        }
    }

    @Override
    public void onInt(Bundle savedInstanceState) {
        populateNavDrawerInfo();

        // Creating items navigation
        mHelpLiveo = new HelpLiveo();
        mHelpLiveo.add(getString(R.string.your_events), R.drawable.ic_event_black_24dp);
        mHelpLiveo.add(getString(R.string.trending), R.drawable.ic_trending_up_black_24dp, 10);
        mHelpLiveo.addSeparator(); // Item separator
        mHelpLiveo.add(getString(R.string.settings), R.drawable.ic_settings_black_24dp);
        mHelpLiveo.add(getString(R.string.add_event), R.drawable.add_note);

        if ( ((SudoMapApplication) getApplication()).getAuthenticateStatus()) {
            with(this) // default theme is OUR THEME
                    .startingPosition(0)
                    .addAllHelpItem(mHelpLiveo.getHelp())
                    .footerItem(R.string.logout, R.drawable.exit_app)
                    .setOnClickUser(onClickEvents)
                    .setOnPrepareOptionsMenu(onPrepare)
                    .setOnClickFooter(onClickFooter)
                    .build();
        }
        else {
            with(this) // default theme is OUR THEME
                    .startingPosition(0)
                    .addAllHelpItem(mHelpLiveo.getHelp())
                    .footerItem(R.string.exit_app, R.drawable.exit_app)
                    .setOnClickUser(onClickEvents)
                    .setOnPrepareOptionsMenu(onPrepare)
                    .setOnClickFooter(onClickFooter)
                    .build();
        }

        mAddEventButton = (FloatingActionButton) findViewById(R.id.fab_add_event);
        mAddEventButton.setImageResource(R.drawable.add_note_white);
        if ( !((SudoMapApplication) getApplication()).getAuthenticateStatus()) {
            mAddEventButton.setVisibility(View.INVISIBLE);
        }

        mAddEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( ((SudoMapApplication) getApplication()).getAuthenticateStatus()) {
                    Intent i = new Intent(HomeActivity.this, AddEventActivity.class);
                    int requestCode = 1;
                    startActivityForResult(i, requestCode);
                }
                else {
                    Toast.makeText(getApplicationContext(), "You are not logged in!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textToSpeechMgr = new TextToSpeechMgr( this );
    }

    @Override //The "R.id.container" should be used in "beginTransaction (). Replace"
    public void onItemClick(int position) {
//        FragmentManager mFragmentManager = getSupportFragmentManager();
        Log.d("nav", "enters onclick listener");
        switch (position){
            case 0:
                Log.d("nav", "0th element (your event) clicked");
                if ( ((SudoMapApplication) getApplication()).getAuthenticateStatus()) {
                    startActivity(new Intent(HomeActivity.this, YourEventActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "You are not logged in!", Toast.LENGTH_SHORT).show();
                }
                closeDrawer();
                break;
            case 1:
                Log.d("nav", "1st element (trending) clicked");
                if ( ((SudoMapApplication) getApplication()).getAuthenticateStatus()) {
                    startActivity(new Intent(HomeActivity.this, TrendingActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "You are not logged in!", Toast.LENGTH_SHORT).show();

                }
                closeDrawer();
                break;
            case 3:
                Log.d("nav", "3rd element (Settings) clicked");
                if ( ((SudoMapApplication) getApplication()).getAuthenticateStatus()) {
                    startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "You are not logged in!", Toast.LENGTH_SHORT).show();
                }
                closeDrawer();
                break;
            case 4:
                Log.d("nav", "4th element (add event) clicked");
                if ( ((SudoMapApplication) getApplication()).getAuthenticateStatus()) {
                    mAddEventButton.performClick();
                }
                else {
                    Toast.makeText(getApplicationContext(), "You are not logged in!", Toast.LENGTH_SHORT).show();
                }
                closeDrawer();
                break;
            default:
//                mFragment = MainFragment.newInstance(mHelpLiveo.get(position).getName());
                break;
        }
////        Fragment mFragment = new MainFragment.newInstance(mHelpLiveo.get(position).getName());
//
//        if (mFragment != null){
//            mFragmentManager.beginTransaction().replace(R.id.container, mFragment).commit();
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mAddEventButton = (FloatingActionButton) findViewById(R.id.fab_add_event);
        mountListNavigation(savedInstanceState);

        if (savedInstanceState != null) {
            isSaveInstance = true;
            setCurrentPosition(savedInstanceState.getInt(CURRENT_POSITION));
            setCurrentCheckPosition(savedInstanceState.getInt(CURRENT_CHECK_POSITION));
        } else {
            ft = getFragmentManager().beginTransaction();
            testFragment newFragment = new testFragment();
            ft.add(R.id.embedded, newFragment);
            ft.commit();
        }

//        if (savedInstanceState == null) {
//            mOnItemClickLiveo.onItemClick(mCurrentPosition);
//        }

        setCheckedItemNavigation(mCurrentCheckPosition, true);


        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        allEventsinFirebase = new ArrayList<Event>();
        allEventsToDisplay = new ArrayList<Event>();
        markerEventHashMap = new HashMap<Marker,Event>();
        //set context for firebase
        ref = new Firebase("https://anchronize.firebaseio.com");
        //create a Firebase reference to the child tree "event"
        Firebase refEvents = ref.child("events");
        Firebase refUsers = ref.child("user");

        //query data once for to get all the events
        refEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                allEventsinFirebase.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Event event = postSnapshot.getValue(Event.class);
                    allEventsinFirebase.add(event);
                }
                addMapMarkers();
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // ShakeDetector initialization from http://jasonmcreynolds.com/?p=388
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
				/*
				 * The following method, "handleShakeEvent(count):" is a stub //
				 * method you would use to setup whatever you want done once the
				 * device has been shook.
				 */
                handleShakeEvent(count);
            }
        });

        textToSpeechMgr = new TextToSpeechMgr( this );

        final HoundifyButton hb = (HoundifyButton) findViewById(R.id.voice_rec_button);
        if(hb!=null)hb.setVisibility(View.INVISIBLE);

        mSearchView = (FloatingSearchView)findViewById(R.id.floating_search_view);
        mSearchView.setOnLeftMenuClickListener(new FloatingSearchView.OnLeftMenuClickListener() {
            @Override
            public void onMenuOpened() {
                Log.d("SearchView", "onMenuOpened()");
                openDrawer();
            }

            @Override
            public void onMenuClosed() {
                Log.d("SearchView", "onMenuClosed()");
                closeDrawer();
            }
        });
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
//                if(item.getTitle().equals("action voice rec")){
                    Log.d("searchview", "voice rec button clicked");
                    hb.performClick();
//                }
            }
        });


        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.homeFAB);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                if(menuItem.getTitle().equals("All")){
                    selectedFilter = "ALL";
                }
                else{
                    selectedFilter = menuItem.getTitle().toString();
                }
                addMapMarkers();
                return true;
            }
        });

    }

    /**
     * Implementation of the PhraseSpotterReader.Listener interface used to handle PhraseSpotter
     * call back.
     */

    protected final PhraseSpotterReader.Listener phraseSpotterListener = new PhraseSpotterReader.Listener() {
        @Override
        public void onPhraseSpotted() {

            // It's important to note that when the phrase spotter detects "Ok Hound" it closes
            // the input stream it was provided.
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    stopPhraseSpotting();
                    // Now start the HomeActivity to begin the search. (Houndify Sample)
                    Houndify.get( HomeActivity.this ).voiceSearch( HomeActivity.this );
                }
            });
        }

        @Override
        public void onError(final Exception ex) {

            // for this sample we don't care about errors from the "Ok Hound" phrase spotter.

        }
    };

    /**
     * Called to start the Phrase Spotter
     */
    protected void startPhraseSpotting() {
        if ( phraseSpotterReader == null ) {
            phraseSpotterReader = new PhraseSpotterReader(new SimpleAudioByteStreamSource());
            phraseSpotterReader.setListener( phraseSpotterListener );
            phraseSpotterReader.start();
        }
    }

    /**
     * Called to stop the Phrase Spotter
     */
    protected void stopPhraseSpotting() {
        if ( phraseSpotterReader != null ) {
            phraseSpotterReader.stop();
            phraseSpotterReader = null;
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        // GoogleMap.setPadding(left, top, right, bottom) - to get control buttons in scope
        mMap.setPadding(0, 200, 0, 0);
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();


        // addMapMarkers();

        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowCloseListener(this);
        //mMap.setOnMarkerDragListener(this);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        lastSelectedMarker = marker;
        return false;
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    protected void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            com.anchronize.sudomap.PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);

            // Getting last location and zooming to that level w/o animation
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));

//            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17) );
            // Customize camera position:https://developers.google.com/maps/documentation/android-api/views#the_camera_position
            CameraPosition currentPosition = new CameraPosition.Builder()
//                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .target(new LatLng(34.0213578, -118.2846286))
                    .zoom(16) // this is the zoom level
                    .bearing(35)   // this is the rotation angle
                    .tilt(40)   // this is the degree of elevation
                    .build();

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(currentPosition));
            mMap.animateCamera(CameraUpdateFactory.scrollBy(-100,-50));

            mMap.setBuildingsEnabled(true);


        }
    }

    @Override
    public boolean onMyLocationButtonClick() {

        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (!((SudoMapApplication)getApplication()).getAuthenticateStatus()) {
            Toast.makeText(getApplicationContext(), "You are not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        Event e = markerEventHashMap.get(lastSelectedMarker);
        Intent i = new Intent(getApplicationContext(), EventDetailActivity.class);
        i.putExtra(EventDetailActivity.EVENT_KEY, e);
        startActivity(i);
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
    }

    private void addMapMarkers(){
        allEventsToDisplay.clear();

        //Filter events
        for(Event e: allEventsinFirebase){
            if(selectedFilter.equals("ALL") || e.getCategory().equals(selectedFilter.toUpperCase())){
                //Registered vs Guest
                if(e.getPrivacy()){
                    if ( ((SudoMapApplication)getApplication()).getAuthenticateStatus() == true){
                        allEventsToDisplay.add(e);
                    }
                }
                else {
                    allEventsToDisplay.add(e);
                }
            }
        }
        //clean up all the marker
        for(Marker marker: markerEventHashMap.keySet()){
            marker.remove();
        }

        //clean up the marker -> Event map
        markerEventHashMap.clear();
        for(Event e : allEventsToDisplay){
            Marker marker;
            switch(e.getCategory()) {
                case "FUN":
                    marker = mMap.addMarker(new MarkerOptions()
                            .anchor(0.0f, 1.0f)
                            .position(new LatLng(e.getLatitude(), e.getLongitude()))
                            .title(e.getTitle())
                            .snippet(e.getCategory())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                    break;
                case "DANGEROUS":
                    marker = mMap.addMarker(new MarkerOptions()
                            .anchor(0.0f, 1.0f)
                            .position(new LatLng(e.getLatitude(), e.getLongitude()))
                            .title(e.getTitle())
                            .snippet(e.getCategory())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    break;
                case "POLICE":
                    marker = mMap.addMarker(new MarkerOptions()
                            .anchor(0.0f, 1.0f)
                            .position(new LatLng(e.getLatitude(), e.getLongitude()))
                            .title(e.getTitle())
                            .snippet(e.getCategory())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                    break;
                case "NEW":
                    marker = mMap.addMarker(new MarkerOptions()
                            .anchor(0.0f, 1.0f)
                            .position(new LatLng(e.getLatitude(), e.getLongitude()))
                            .title(e.getTitle())
                            .snippet(e.getCategory())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    break;
                case "FIRE":
                    marker = mMap.addMarker(new MarkerOptions()
                            .anchor(0.0f, 1.0f)
                            .position(new LatLng(e.getLatitude(), e.getLongitude()))
                            .title(e.getTitle())
                            .snippet(e.getCategory())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    break;
                default:
                    marker = mMap.addMarker(new MarkerOptions()
                            .anchor(0.0f, 1.0f)
                            .position(new LatLng(e.getLatitude(), e.getLongitude()))
                            .title(e.getTitle())
                            .snippet(e.getCategory())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    break;

            }
            markerEventHashMap.put(marker, e);
        }
    }

    // End of Google Maps API call

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (com.anchronize.sudomap.PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    protected void showMissingPermissionError() {
        com.anchronize.sudomap.PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    protected void configureFindView(){
        mList = (ListView) findViewById(br.liveo.navigationliveo.R.id.list);
        mList.setOnItemClickListener(new DrawerItemClickListener());

        mToolbar = (Toolbar) findViewById(br.liveo.navigationliveo.R.id.toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(br.liveo.navigationliveo.R.id.drawerLayout);

        mDrawerToggle = new ActionBarDrawerToggleCompat(this, mDrawerLayout, mToolbar);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mTitleFooter = (TextView) this.findViewById(br.liveo.navigationliveo.R.id.titleFooter);
        mIconFooter = (ImageView) this.findViewById(br.liveo.navigationliveo.R.id.iconFooter);

        mTitleSecondFooter = (TextView) this.findViewById(br.liveo.navigationliveo.R.id.titleSecondFooter);
        mIconSecondFooter = (ImageView) this.findViewById(br.liveo.navigationliveo.R.id.iconSecondFooter);

        mFooterDrawer = (RelativeLayout) this.findViewById(br.liveo.navigationliveo.R.id.footerDrawer);
        mFooterSecondDrawer = (RelativeLayout) this.findViewById(br.liveo.navigationliveo.R.id.footerSecondDrawer);
        mRelativeDrawer = (ScrimInsetsFrameLayout) this.findViewById(br.liveo.navigationliveo.R.id.relativeDrawer);

        this.setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                if (!mRemoveHeader || !mCustomHeader) {
                    Resources.Theme theme = this.getTheme();
                    TypedArray typedArray = theme.obtainStyledAttributes(new int[]{android.R.attr.colorPrimary});
                    mDrawerLayout.setStatusBarBackground(typedArray.getResourceId(0, 0));
                }
            } catch (Exception e) {
                e.getMessage();
            }
//
//            this.setElevationToolBar(mElevationToolBar);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_POSITION, mCurrentPosition);
        outState.putInt(CURRENT_CHECK_POSITION, mCurrentCheckPosition);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mDrawerToggle != null) {
            if (mDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (mOnPrepareOptionsMenu != null){
            boolean drawerOpen = mDrawerLayout.isDrawerOpen(mRelativeDrawer);
            mOnPrepareOptionsMenu.onPrepareOptionsMenu(menu, mCurrentPosition, drawerOpen);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    protected class ActionBarDrawerToggleCompat extends ActionBarDrawerToggle {

        public ActionBarDrawerToggleCompat(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar){
            super(
                    activity,
                    drawerLayout, toolbar,
                    br.liveo.navigationliveo.R.string.drawer_open,
                    br.liveo.navigationliveo.R.string.drawer_close);
        }

        @Override
        public void onDrawerClosed(View view) {
            supportInvalidateOptionsMenu();
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            supportInvalidateOptionsMenu();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    protected class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Log.d("nav", "HEY I GOT CLICKED!");
            int mPosition = (!mRemoveHeader || !mCustomHeader ? position - 1 : position);

            if (mPosition == -1){
                mDrawerLayout.closeDrawer(mRelativeDrawer);
                return;
            }

            HelpItem helpItem = mHelpItem.get(mPosition);
            if (!helpItem.isHeader()) {
                if (position != 0 || (mRemoveHeader && mCustomHeader)) {
//                    setCurrentPosition(mPosition);

//                    if (helpItem.isCheck()) {
//                        setCurrentCheckPosition(mPosition);
//                        setCheckedItemNavigation(mPosition, true);
//                    }
                    mOnItemClickLiveo.onItemClick(mPosition);
                }

                mDrawerLayout.closeDrawer(mRelativeDrawer);
            }
        }
    }

//    protected void mountListNavigation(Bundle savedInstanceState){
//        if (mOnItemClickLiveo == null){
//            this.createUserDefaultHeader();
//            this.onInt(savedInstanceState);
//        }
//    }



    public void build(){

        if (mOnItemClickLiveo == null){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.start_navigation_listener));
        }

        this.addHeaderView();
        List<Integer> mListExtra = new ArrayList<>();
        mListExtra.add(0, mNewSelector);
        mListExtra.add(1, mColorDefault);
        mListExtra.add(2, mColorIcon);
        mListExtra.add(3, mColorName);
        mListExtra.add(4, mColorSeparator);
        mListExtra.add(5, mColorCounter);
        mListExtra.add(6, mSelectorDefault);
        mListExtra.add(7, mColorSubHeader);

        List<Boolean> mListRemove = new ArrayList<>();
        mListRemove.add(0, mRemoveAlpha);
        mListRemove.add(1, mRemoveColorFilter);

        if (mHelpItem != null){
            mNavigationAdapter = new NavigationLiveoAdapter(this, NavigationLiveoList.getNavigationAdapter(this, mHelpItem, mNavigation.colorSelected, mNavigation.removeSelector), mListRemove, mListExtra);
        }else {
            mNavigationAdapter = new NavigationLiveoAdapter(this, NavigationLiveoList.getNavigationAdapter(this, mNavigation), mListRemove, mListExtra);
        }

        setAdapter();
    }

    protected void setAdapter(){
        if (mNavigationAdapter != null){
            mList.setAdapter(mNavigationAdapter);
        }
    }

//    /**
//     * Create user default header
//     */
//    protected void createUserDefaultHeader() {
//        mHeader = getLayoutInflater().inflate(br.liveo.navigationliveo.R.layout.navigation_list_header, mList, false);
//
//        userName = (TextView) mHeader.findViewById(br.liveo.navigationliveo.R.id.userName);
//        userEmail = (TextView) mHeader.findViewById(br.liveo.navigationliveo.R.id.userEmail);
//        userPhoto = (ImageView) mHeader.findViewById(br.liveo.navigationliveo.R.id.userPhoto);
//        userBackground = (ImageView) mHeader.findViewById(br.liveo.navigationliveo.R.id.userBackground);
//    }

    protected void addHeaderView() {
        if(!this.mRemoveHeader) {
            this.mList.addHeaderView(this.mHeader);
            mRelativeDrawer.setFitsSystemWindows(true);
        }
    }

    /**
     * Remove Header
     */
    public NavigationLiveo removeHeader(){
        mRemoveHeader = true;
        mCustomHeader = true;
        mRelativeDrawer.setFitsSystemWindows(false);
        return this;
    }

    /**
     * Remove elevation toolBar
     */
    public NavigationLiveo removeElevationToolBar(){
        this.mElevationToolBar = 0;
        return this;
    }

    /**
     * Background ListView
     * @param color Default color - R.color.nliveo_white
     */
    public NavigationLiveo backgroundList(int color){
        this.mSelectorDefault = color;
        this.mList.setBackgroundResource(color);
        this.mFooterDrawer.setBackgroundResource(color);
        this.mFooterSecondDrawer.setBackgroundResource(color);
        return this;
    }

    /**
     * Background Footer
     * @param color Default color - R.color.nliveo_white
     */
    public NavigationLiveo footerBackground(int color){
        this.mFooterDrawer.setBackgroundResource(color);
        this.mFooterSecondDrawer.setBackgroundResource(color);
        return this;
    }

    /**
     * Starting listener navigation
     * @param listener listener.
     */
    public NavigationLiveo with(OnItemClickListener listener){
        setContentView(R.layout.activity_home);
        this.mOnItemClickLiveo = listener;
        configureFindView();
        return this;
    };

//    /**
//     * Starting listener navigation
//     * @param listener listener.
//     * @param theme theme.
//     */
//    public NavigationLiveo with(OnItemClickListener listener, int theme){
////        setContentView(theme == Navigation.THEME_DARK ? br.liveo.navigationliveo.R.layout.navigation_main_dark : br.liveo.navigationliveo.R.layout.navigation_main_light);
//        setContentView(R.layout.activity_home);
//        this.mOnItemClickLiveo = listener;
//        configureFindView();
//        return this;
//    };

    /**
     * @param listHelpItem list HelpItem.
     */
    @Override
    public NavigationLiveo addAllHelpItem(List<HelpItem> listHelpItem){
        this.mHelpItem = listHelpItem;
        return this;
    }

    /**
     * @param listNameItem list name item.
     */
    public NavigationLiveo nameItem(List<String> listNameItem){
        this.mNavigation.nameItem = listNameItem;
        return this;
    }

    /**
     * @param listIcon list icon item.
     */
    public NavigationLiveo iconItem(List<Integer> listIcon){
        this.mNavigation.iconItem = listIcon;
        return this;
    }

    /**
     * @param listHeader list header name item.
     */
    public NavigationLiveo headerItem(List<Integer> listHeader){
        this.mNavigation.headerItem = listHeader;
        return this;
    }

    /**
     * @param sparceCount sparce count item.
     */
    public NavigationLiveo countItem(SparseIntArray sparceCount){
        this.mNavigation.countItem = sparceCount;
        return this;
    }

    /**
     * Set adapter attributes
     * @param listNameItem list name item.
     * @param listIcon list icon item.
     * @param listItensHeader list header name item.
     * @param sparceItensCount sparce count item.
     */
    public void setNavigationAdapter(List<String> listNameItem, List<Integer> listIcon, List<Integer> listItensHeader, SparseIntArray sparceItensCount){
        this.nameItem(listNameItem);
        this.iconItem(listIcon);
        this.headerItem(listItensHeader);
        this.countItem(sparceItensCount);
    }

    /**
     * Set adapter attributes
     * @param listNameItem list name item.
     * @param listIcon list icon item.
     */
    public void setNavigationAdapter(List<String> listNameItem, List<Integer> listIcon){
        this.nameItem(listNameItem);
        this.iconItem(listIcon);
    }

    /**
     * hide navigation item
     * @param listHide list hide item.
     */
    public NavigationLiveo hideItem(List<Integer> listHide){
        mNavigation.hideItem = listHide;
        return this;
    }

    /**
     * show navigation item
     * @param listShow list show item.
     */
    public void showNavigationItem(List<Integer> listShow){

        if (listShow == null){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.list_hide_item));
        }

        for (int i = 0; i < listShow.size(); i++){
            setVisibleItemNavigation(listShow.get(i), true);
        }
    }

    /**
     * Starting listener navigation
     * @param onItemClick listener.
     * @deprecated
     */
    public void setNavigationListener(OnItemClickListener onItemClick){
        this.mOnItemClickLiveo = onItemClick;
    };

    /**
     * First item of the position selected from the list, use method startingPosition
     * @param position ...
     * @deprecated
     */
    public void setDefaultStartPositionNavigation(int position){
        if (!isSaveInstance) {
            this.mCurrentPosition = position;
            this.mCurrentCheckPosition = position;
        }
    }

    /**
     * First item of the position selected from the list
     * @param position ...
     */
    @Override
    public NavigationLiveo startingPosition(int position){
        if (!isSaveInstance) {
            this.mCurrentPosition = position;
            this.mCurrentCheckPosition = position;
        }

        return this;
    }

    /**
     * Position in the last clicked item list
     * @param position ...
     */
    protected void setCurrentPosition(int position){
        this.mCurrentPosition = position;
    }

    /**
     * get position in the last clicked item list
     */
    public int getCurrentPosition(){
        return this.mCurrentPosition;
    }

    /**
     * Position in the last clicked item list check
     * @param position ...
     */
    protected void setCurrentCheckPosition(int position){
        this.mCurrentCheckPosition = position;
    }

    /**
     * get position in the last clicked item list check
     */
    public int getCurrentCheckPosition(){
        return this.mCurrentCheckPosition;
    }

    /*{  }*/

    /**
     * Select item clicked
     * @param position item position.
     * @param checked true to check.
     */
    public void setCheckedItemNavigation(int position, boolean checked){

        if (this.mNavigationAdapter == null){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.start_navigation_listener));
        }

        this.mNavigationAdapter.resetarCheck();
        this.mNavigationAdapter.setChecked(position, checked);
    }

    /**
     * Information footer list item
     * @param title item footer name.
     * @param icon item footer icon.
     * @deprecated
     */
    public void setFooterInformationDrawer(String title, int icon){

        if (title == null){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        if (title.trim().equals("")){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        mTitleFooter.setText(title);

        if (icon == 0){
            mIconFooter.setVisibility(View.GONE);
        }else{
            mIconFooter.setImageResource(icon);
        }
    };

    /**
     * Information footer list item
     * @param title item footer name.
     * @param icon item footer icon.
     */
    public NavigationLiveo footerItem(String title, int icon){

        if (title == null){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        if (title.trim().equals("")){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        mTitleFooter.setText(title);

        if (icon == 0){
            mIconFooter.setVisibility(View.GONE);
        }else{
            mIconFooter.setImageResource(icon);
        }

        return this;
    };

    /**
     * Information footer second list item
     * @param title item footer name.
     * @param icon item footer icon.
     */
    public NavigationLiveo footerSecondItem(String title, int icon){

        if (title == null){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        if (title.trim().equals("")){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        mTitleSecondFooter.setText(title);

        if (icon == 0){
            mIconSecondFooter.setVisibility(View.GONE);
        }else{
            mIconSecondFooter.setImageResource(icon);
        }

        mFooterSecondDrawer.setVisibility(View.VISIBLE);
        return this;
    };

    /**
     * Information footer list item
     * @param title item footer name.
     * @param icon item footer icon.
     * @param colorName item footer name color.
     * @param colorIcon item footer icon color.
     * @deprecated
     */
    public void setFooterInformationDrawer(String title, int icon, int colorName, int colorIcon){

        if (title == null){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        if (title.trim().equals("")){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        mTitleFooter.setText(title);

        if (colorName > 0){
            mTitleFooter.setTextColor(ContextCompat.getColor(this, colorName));
        }

        if (icon == 0){
            mIconFooter.setVisibility(View.GONE);
        }else{
            mIconFooter.setImageResource(icon);

            if ( colorIcon > 0) {
                mIconFooter.setColorFilter(ContextCompat.getColor(this, colorIcon));
            }
        }
    };

    /**
     * Information footer list item
     * @param title item footer name.
     * @param icon item footer icon.
     * @param colorName item footer name color.
     * @param colorIcon item footer icon color.
     */
    public NavigationLiveo footerInformationDrawer(String title, int icon, int colorName, int colorIcon){

        if (title == null){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        if (title.trim().equals("")){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        mTitleFooter.setText(title);

        if (colorName > 0){
            mTitleFooter.setTextColor(ContextCompat.getColor(this, colorName));
        }

        if (icon == 0){
            mIconFooter.setVisibility(View.GONE);
        }else{
            mIconFooter.setImageResource(icon);

            if ( colorIcon > 0) {
                mIconFooter.setColorFilter(ContextCompat.getColor(this, colorIcon));
            }
        }
        return this;
    };

    /**
     * Information footer list item
     * @param title item footer name.
     * @param icon item footer icon.
     * @deprecated
     */
    public NavigationLiveo setFooterInformationDrawer(int title, int icon){

        if (title == 0){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        mTitleFooter.setText(getString(title));

        if (icon == 0){
            mIconFooter.setVisibility(View.GONE);
        }else {
            mIconFooter.setImageResource(icon);
        }

        if (mColorDefault > 0){
            footerNameColor(mColorDefault);
            footerIconColor(mColorDefault);
        }

        return this;
    };

    /**
     * Information footer list item
     * @param title item footer name.
     * @param icon item footer icon.
     */
    public NavigationLiveo footerItem(int title, int icon){

        if (title == 0){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        mTitleFooter.setText(getString(title));

        if (icon == 0){
            mIconFooter.setVisibility(View.GONE);
        }else{
            mIconFooter.setImageResource(icon);
        }

        if (mColorDefault > 0){
            footerNameColor(mColorDefault);
            footerIconColor(mColorDefault);
        }

        return this;
    };

    /**
     * Information footer list item
     * @param title item footer name.
     * @param icon item footer icon.
     */
    public NavigationLiveo footerSecondItem(int title, int icon){

        if (title == 0){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        mTitleSecondFooter.setText(getString(title));

        if (icon == 0){
            mIconSecondFooter.setVisibility(View.GONE);
        }else{
            mIconSecondFooter.setImageResource(icon);
        }

        if (mColorDefault > 0){
            footerSecondNameColor(mColorDefault);
            footerSecondIconColor(mColorDefault);
        }

        mFooterSecondDrawer.setVisibility(View.VISIBLE);

        return this;
    };

    /**
     * Information footer list item
     * @param title item footer name.
     * @param icon item footer icon.
     * @param colorName item footer name color.
     * @param colorIcon item footer icon color.
     * @deprecated
     */
    public void setFooterInformationDrawer(int title, int icon, int colorName, int colorIcon){

        if (title == 0){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        mTitleFooter.setText(title);

        if (colorName > 0) {
            mTitleFooter.setTextColor(ContextCompat.getColor(this, colorName));
        }

        if (icon == 0){
            mIconFooter.setVisibility(View.GONE);
        }else {
            mIconFooter.setImageResource(icon);

            if ( colorIcon > 0) {
                mIconFooter.setColorFilter(ContextCompat.getColor(this, colorIcon));
            }
        }
    };

    /**
     * Information footer list item
     * @param title item footer name.
     * @param icon item footer icon.
     * @param colorName item footer name color.
     * @param colorIcon item footer icon color.
     */
    public NavigationLiveo footerItem(int title, int icon, int colorName, int colorIcon){

        if (title == 0){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        mTitleFooter.setText(title);

        if (colorName > 0){
            mTitleFooter.setTextColor(ContextCompat.getColor(this, colorName));
        }

        if (icon == 0){
            mIconFooter.setVisibility(View.GONE);
        }else{
            mIconFooter.setImageResource(icon);

            if ( colorIcon > 0) {
                mIconFooter.setColorFilter(ContextCompat.getColor(this, colorIcon));
            }
        }
        return this;
    };

    /**
     * Information footer second list item
     * @param title item footer name.
     * @param icon item footer icon.
     * @param colorName item footer name color.
     * @param colorIcon item footer icon color.
     */
    public NavigationLiveo footerSecondItem(int title, int icon, int colorName, int colorIcon){

        if (title == 0){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        mTitleSecondFooter.setText(title);

        if (colorName > 0){
            mTitleSecondFooter.setTextColor(ContextCompat.getColor(this, colorName));
        }

        if (icon == 0){
            mIconSecondFooter.setVisibility(View.GONE);
        }else{
            mIconSecondFooter.setImageResource(icon);

            if ( colorIcon > 0) {
                mIconSecondFooter.setColorFilter(ContextCompat.getColor(this, colorIcon));
            }
        }

        mFooterSecondDrawer.setVisibility(View.VISIBLE);
        return this;
    };

    /**
     * Information footer list item
     * @param title item footer name.
     * @param icon item footer icon.
     * @param color item footer name and icon color.
     */
    public NavigationLiveo footerItem(int title, int icon, int color){

        if (title == 0){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        mTitleFooter.setText(title);

        if (color > 0){
            mTitleFooter.setTextColor(ContextCompat.getColor(this, color));
        }

        if (icon == 0){
            mIconFooter.setVisibility(View.GONE);
        }else{
            mIconFooter.setImageResource(icon);

            if ( color > 0) {
                mIconFooter.setColorFilter(ContextCompat.getColor(this, color));
            }
        }
        return this;
    };

    /**
     * Information footer second list item
     * @param title item footer name.
     * @param icon item footer icon.
     * @param color item footer name and icon color.
     */
    public NavigationLiveo footerSecondItem(int title, int icon, int color){

        if (title == 0){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        mTitleSecondFooter.setText(title);

        if (color > 0){
            mTitleSecondFooter.setTextColor(ContextCompat.getColor(this, color));
        }

        if (icon == 0){
            mIconSecondFooter.setVisibility(View.GONE);
        }else{
            mIconSecondFooter.setImageResource(icon);

            if ( color > 0) {
                mIconSecondFooter.setColorFilter(ContextCompat.getColor(this, color));
            }
        }

        mFooterSecondDrawer.setVisibility(View.VISIBLE);
        return this;
    };

    /**
     * Information footer list item
     * @param title item footer name.
     * @param icon item footer icon.
     * @param color item footer name and icon color.
     */
    public NavigationLiveo footerItem(String title, int icon, int color){

        if (title == null){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        if (title.trim().equals("")){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        mTitleFooter.setText(title);

        if (color > 0){
            mTitleFooter.setTextColor(ContextCompat.getColor(this, color));
        }

        if (icon == 0){
            mIconFooter.setVisibility(View.GONE);
        }else{
            mIconFooter.setImageResource(icon);

            if ( color > 0) {
                mIconFooter.setColorFilter(ContextCompat.getColor(this, color));
            }
        }
        return this;
    };

    /**
     * Information footer second list item
     * @param title item footer name.
     * @param icon item footer icon.
     * @param color item footer name and icon color.
     */
    public NavigationLiveo footerSecondItem(String title, int icon, int color){

        if (title == null){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        if (title.trim().equals("")){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.title_null_or_empty));
        }

        mTitleSecondFooter.setText(title);

        if (color > 0){
            mTitleSecondFooter.setTextColor(ContextCompat.getColor(this, color));
        }

        if (icon == 0){
            mIconSecondFooter.setVisibility(View.GONE);
        }else{
            mIconSecondFooter.setImageResource(icon);

            if ( color > 0) {
                mIconSecondFooter.setColorFilter(ContextCompat.getColor(this, color));
            }
        }
        return this;
    };

    /**
     * If not want to use the footer item just put false
     * @param visible true or false.
     */
    public void setFooterNavigationVisible(boolean visible){
        this.mFooterDrawer.setVisibility((visible) ? View.VISIBLE : View.GONE);
    }

    /**
     * Remove footer
     */
    public NavigationLiveo removeFooter(){
        this.mFooterDrawer.setVisibility(View.GONE);
        this.mFooterSecondDrawer.setVisibility(View.GONE);
        return this;
    }

    /**
     * Item color selected in the list - name and icon (use before the setNavigationAdapter)
     * @param colorId color id.
     * @deprecated
     */
    public void setColorSelectedItemNavigation(int colorId){
        mNavigation.colorSelected = colorId;
    }

    /**
     * Item color selected in the list - name, icon and counter
     * @param colorId color id.
     */
    public NavigationLiveo colorItemSelected(int colorId){
        mNavigation.colorSelected = colorId;
        return this;
    }

    /**
     * Footer name color
     * @param colorId color id.
     * @deprecated
     */
    public void setFooterNameColorNavigation(int colorId){
        this.mTitleFooter.setTextColor(ContextCompat.getColor(this, colorId));
        this.mTitleFooter.setTextColor(ContextCompat.getColor(this, colorId));
    }

    /**
     * Footer name color
     * @param colorId color id.
     */
    public NavigationLiveo footerNameColor(int colorId){
        this.mTitleFooter.setTextColor(ContextCompat.getColor(this, colorId));
        return this;
    }

    /**
     * Footer second name color
     * @param colorId color id.
     */
    public NavigationLiveo footerSecondNameColor(int colorId){
        this.mTitleSecondFooter.setTextColor(ContextCompat.getColor(this, colorId));
        return this;
    }


    /**
     * Footer icon color
     * @param colorId color id.
     */
    public NavigationLiveo footerIconColor(int colorId) {
        this.mIconFooter.setColorFilter(ContextCompat.getColor(this, colorId));
        return this;
    }

    /**
     * Footer second icon color
     * @param colorId color id.
     */
    public NavigationLiveo footerSecondIconColor(int colorId) {
        this.mIconSecondFooter.setColorFilter(ContextCompat.getColor(this, colorId));
        return this;
    }


    /**
     * Footer icon color
     * @param colorId color id.
     * @deprecated
     */
    public void setFooterIconColorNavigation(int colorId) {
        this.mIconFooter.setColorFilter(ContextCompat.getColor(this, colorId));
    }

    /**
     * Item color default in the list - name and icon (use before the setNavigationAdapter)
     * @param colorId color id.
     * @deprecated
     */
    public void setColorDefaultItemNavigation(int colorId){
        this.mColorDefault = colorId;
    }

    /**
     * Item color default in the list - name and icon (use before the setNavigationAdapter)
     * @param colorId color id.
     */
    public NavigationLiveo colorItemDefault(int colorId){
        this.mColorDefault = colorId;
        return this;
    }

    /**
     * Icon item color in the list - icon (use before the setNavigationAdapter)
     * @param colorId color id.
     * @deprecated
     */
    public void setColorIconItemNavigation(int colorId){
        this.mColorIcon = colorId;
    }

    /**
     * Icon item color in the list - icon (use before the setNavigationAdapter)
     * @param colorId color id.
     */
    public NavigationLiveo colorItemIcon(int colorId){
        this.mColorIcon = colorId;
        return this;
    }

    /**
     * Separator item subHeader color in the list - icon (use before the setNavigationAdapter)
     * @param colorId color id.
     * @deprecated
     */
    public void setColorSeparatorItemSubHeaderNavigation(int colorId){
        this.mColorSeparator = colorId;
    }

    /**
     * Name item subHeader color
     * @param colorId color id.
     */
    public NavigationLiveo colorNameSubHeader(int colorId){
        this.mColorSubHeader = colorId;
        return this;
    }

    /**
     * Separator item subHeader color in the list - icon
     * @param colorId color id.
     */
    public NavigationLiveo colorLineSeparator(int colorId){
        this.mColorSeparator = colorId;
        return this;
    }

    /**
     * Counter color in the list (use before the setNavigationAdapter)
     * @param colorId color id.
     * @deprecated
     */
    public void setColorCounterItemNavigation(int colorId){
        this.mColorCounter = colorId;
    }

    /**
     * Counter color in the list (use before the setNavigationAdapter)
     * @param colorId color id.
     */
    public NavigationLiveo colorItemCounter(int colorId){
        this.mColorCounter = colorId;
        return this;
    }

    /**
     * Name item color in the list - name (use before the setNavigationAdapter)
     * @param colorId color id.
     * @deprecated
     */
    public void setColorNameItemNavigation(int colorId){
        this.mColorName = colorId;
    }

    /**
     * Name item color in the list - name (use before the setNavigationAdapter)
     * @param colorId color id.
     */
    public NavigationLiveo colorItemName(int colorId){
        this.mColorName = colorId;
        return this;
    }

    /**
     * New selector navigation
     * @param resourceSelector drawable xml - selector.
     * @deprecated
     */
    public void setNewSelectorNavigation(int resourceSelector){

        if (mNavigation.removeSelector){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.remove_selector_navigation));
        }

        this.mNewSelector = resourceSelector;
    }

    /**
     * New selector navigation
     * @param resourceSelector drawable xml - selector.
     */
    public NavigationLiveo selectorCheck(int resourceSelector){

        if (mNavigation.removeSelector){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.remove_selector_navigation));
        }

        this.mNewSelector = resourceSelector;
        return this;
    }

    /**
     * Remove selector navigation
     * @deprecated
     */
    public void removeSelectorNavigation(){
        mNavigation.removeSelector = true;
    }

    /**
     * Remove selector navigation
     */
    public NavigationLiveo removeSelector(){
        mNavigation.removeSelector = true;
        return this;
    }

    /**
     * New name item
     * @param position item position.
     * @param name new name
     */
    public void setNewName(int position, String name){
        this.mNavigationAdapter.setNewName(position, name);
    }

    /**
     * New name item
     * @param position item position.
     * @param name new name
     */
    public void setNewName(int position, int name){
        this.mNavigationAdapter.setNewName(position, getString(name));
    }

    /**
     * New name item
     * @param position item position.
     * @param icon new icon
     */
    public void setNewIcon(int position, int icon){
        this.mNavigationAdapter.setNewIcon(position, icon);
    }

    /**
     * New information item navigation
     * @param position item position.
     * @param name new name
     * @param icon new icon
     * @param counter new counter
     */
    public void setNewInformationItem(int position, int name, int icon, int counter) {
        this.mNavigationAdapter.setNewInformationItem(position, getString(name), icon, counter);
    }

    /**
     * New information item navigation
     * @param position item position.
     * @param name new name
     * @param icon new icon
     * @param counter new counter
     */

    public void setNewInformationItem(int position, String name, int icon, int counter) {
        this.mNavigationAdapter.setNewInformationItem(position, name, icon, counter);
    }

    /**
     * New counter value
     * @param position item position.
     * @param value new counter value.
     */
    public void setNewCounterValue(int position, int value){
        this.mNavigationAdapter.setNewCounterValue(position, value);
    }

    /**
     * Increasing counter value
     * @param position item position.
     * @param value new counter value (old value + new value).
     */
    public void setIncreasingCounterValue(int position, int value){
        this.mNavigationAdapter.setIncreasingCounterValue(position, value);
    }

    /**
     * Decrease counter value
     * @param position item position.
     * @param value new counter value (old value - new value).
     */
    public void setDecreaseCountervalue(int position, int value){
        this.mNavigationAdapter.setDecreaseCountervalue(position, value);
    }

    /**
     * Make the item visible navigation or not (default value is true)
     * @param position item position.
     * @param visible true or false.
     */
    public void setVisibleItemNavigation(int position, boolean visible){
        this.mNavigationAdapter.setVisibleItemNavigation(position, visible);
    }

    /**
     * Remove alpha item navigation (use before the setNavigationAdapter)
     * @deprecated
     */
    public void removeAlphaItemNavigation(){
        this.mRemoveAlpha = !mRemoveAlpha;
    }

    /**
     * Remove alpha item navigation (use before the setNavigationAdapter)
     */
    public NavigationLiveo removeAlpha(){
        this.mRemoveAlpha = !mRemoveAlpha;
        return this;
    }

    /**
     * Remove color filter icon item navigation
     */
    public NavigationLiveo removeColorFilter(){
        this.mRemoveColorFilter = !mRemoveColorFilter;
        return this;
    }

    /**
     * public void setElevation (float elevation)
     * Added in API level 21
     * Default value is 15
     * @param elevation Sets the base elevation of this view, in pixels.
     */
    public void setElevationToolBar(float elevation){
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            this.mElevationToolBar = elevation;
//            this.getToolbar().setElevation(elevation);
//        }
    }

    /**
     * Remove default Header
     */
    public void showDefaultHeader() {
        if (mHeader == null){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.header_not_created));
        }

        mList.addHeaderView(mHeader);
    }

    /**
     * Remove default Header
     */
    protected void removeDefaultHeader() {
        if (mHeader == null){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.header_not_created));
        }

        mList.removeHeaderView(mHeader);
    }

    /**
     * Add custom Header
     * @param v ...
     * @deprecated
     */
    public void addCustomHeader(View v) {
        if (v == null){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.custom_header_not_created));
        }

        removeDefaultHeader();
        mList.addHeaderView(v);
    }

    /**
     * Add custom Header
     * @param view ...
     */
    public NavigationLiveo customHeader(View view) {
        if (view == null){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.custom_header_not_created));
        }

        this.removeHeader();
        mCustomHeader = false;
        mRelativeDrawer.setFitsSystemWindows(true);
        mList.addHeaderView(view);
        return this;
    }

    /**
     * Remove default Header
     * @param v ...
     */
    public void removeCustomdHeader(View v) {
        if (v == null){
            throw new RuntimeException(getString(br.liveo.navigationliveo.R.string.custom_header_not_created));
        }

        mList.removeHeaderView(v);
    }

    /**
     * get listview
     */
    public ListView getListView() {
        return this.mList;
    }

    /**
     * get toolbar
     */
    public Toolbar getToolbar() {
        return this.mToolbar;
    }

    /**
     * is drawer open
     */
    public boolean isDrawerOpen(){
        return mDrawerLayout.isDrawerOpen(mRelativeDrawer);
    }

    /**
     * Open drawer
     */
    public void openDrawer() {
        if (((SudoMapApplication) getApplication()).getAuthenticateStatus()) {
            this.userName.setText(((SudoMapApplication) getApplication()).getCurrentUsername());
            if (((SudoMapApplication) getApplication()).getCurrentUser().getProfileImageBitMap() != null) {
                if (!((SudoMapApplication) getApplication()).getCurrentUser().getProfileImageBitMap().equals("default"))
                    this.userPhoto.setImageBitmap(((SudoMapApplication) getApplication()).getCurrentUser().getProfileImageBitMap());
            }
        } else {
            this.userName.setText("Guest");
//            this.userPhoto.setImageBitmap(((SudoMapApplication) getApplication()).getCurrentUser().getProfileImageBitMap());
        }

        mDrawerLayout.openDrawer(mRelativeDrawer);
    }

    /**
     * Close drawer
     */
    public void closeDrawer() {
        mSearchView.closeMenu(false);
        mDrawerLayout.closeDrawer(mRelativeDrawer);
    }

    public NavigationLiveo setOnItemClick(OnItemClickListener onItemClick){
        this.mOnItemClickLiveo = onItemClick;
        return this;
    }

    public NavigationLiveo setOnPrepareOptionsMenu(OnPrepareOptionsMenuLiveo onPrepareOptionsMenu){
        this.mOnPrepareOptionsMenu = onPrepareOptionsMenu;
        return this;
    }

    public NavigationLiveo setOnClickUser(View.OnClickListener listener){
        this.userPhoto.setOnClickListener(listener);
        return this;
    }

    public NavigationLiveo setOnClickFooter(View.OnClickListener listener){
        this.mFooterDrawer.setOnClickListener(listener);
        return this;
    }

    public NavigationLiveo setOnClickFooterSecond(View.OnClickListener listener){
        this.mFooterSecondDrawer.setOnClickListener(listener);
        return this;
    }

    @Override
    public void onBackPressed() {

        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mRelativeDrawer);
        if (drawerOpen) {
            mDrawerLayout.closeDrawer(mRelativeDrawer);
        } else {
            super.onBackPressed();
        }
    }

    // Response options
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case RESULT_OK:
                Log.d("EVENT", "event successfully added");
                addMapMarkers();
                break;
            case Houndify.REQUEST_CODE:
                Log.d("Houndify", "Gets response");
                final HoundSearchResult result = Houndify.get(this).fromActivityResult(resultCode, data);

                if (result.hasResult()) {
                    onResponse( result.getResponse() );
                }
                else if (result.getErrorType() != null) {
//                    textToSpeechMgr.speak("Sorry, I didn't get that.");
                    onError(result.getException(), result.getErrorType());
                }
                else {
                    Log.d("HoundifyRes","Aborted search");
                }
                break;
            default:
                break;
        }
    }

    /**
     * Called from onActivityResult() above (Houndify sample)
     *
     * @param response
     */
    protected void onResponse(final HoundResponse response) {
        if (response.getResults().size() > 0) {
            // Required for conversational support
            StatefulRequestInfoFactory.get(this).setConversationState(response.getResults().get(0).getConversationState());

            Log.d("HoundifyRes","Received response\n\n" + response.getResults().get(0).getWrittenResponse());
//            textToSpeechMgr.speak(response.getResults().get(0).getSpokenResponse());

            /**
             * "Client Match" analysis code. Modified from sample app
             *
             * Houndify client apps can specify their own custom phrases which they want matched using
             * the "Client Match" feature. This section of code demonstrates how to handle
             * a "Client Match phrase".  To enable this demo first open the
             * StatefulRequestInfoFactory.java file in this project and and uncomment the
             * "Client Match" demo code there.
             *
             */
            if ( response.getResults().size() > 0 ) {
                CommandResult commandResult = response.getResults().get( 0 );
                if ( commandResult.getCommandKind().equals("ClientMatchCommand")) {
                    JsonNode matchedItemNode = commandResult.getJsonNode().findValue("MatchedItem");
                    String intentValue = matchedItemNode.findValue( "Intent").textValue();

                    if ( intentValue.equals("TURN_LIGHT_ON") ) {
                        textToSpeechMgr.speak("Client match TURN LIGHT ON successful");
                    }
                    else if ( intentValue.equals("TURN_LIGHT_OFF") ) {
                        textToSpeechMgr.speak("Client match TURN LIGHT OFF successful");
                    }
                    else if ( intentValue.equals("ADD_EVENT") ) {
//                        textToSpeechMgr.speak("Client match ADD NEW EVENT successful");
                        if ( ((SudoMapApplication) getApplication()).getAuthenticateStatus()) {
                            textToSpeechMgr.speak(response.getResults().get(0).getSpokenResponse());
                            textToSpeechMgr.speak("Client match ADD NEW EVENT successful");
                            Intent i = new Intent(HomeActivity.this, AddEventActivity.class);
                            int requestCode = 1;
                            startActivityForResult(i, requestCode);
                        }
                        else {
                            textToSpeechMgr.speak("Sorry, but you are not logged in.");
                        }
                    }
                }
                else {
                    // Actual error message
//                    textToSpeechMgr.speak(response.getResults().get(0).getWrittenResponse());
                    textToSpeechMgr.speak("Sorry, I didn't get that.");
                }
            }
        }
        else {
            textToSpeechMgr.speak("Sorry, I didn't get that.");
            Log.d("HoundifyRes","Received empty response!");
        }
    }

    /**
     * Called from onActivityResult() above
     *
     * @param ex
     * @param errorType
     */
    protected void onError(final Exception ex, final VoiceSearchInfo.ErrorType errorType) {
        Log.d("HoudifyRes", errorType.name() + "\n\n" + exceptionToString(ex));
    }

    protected static String exceptionToString(final Exception ex) {
        try {
            final StringWriter sw = new StringWriter(1024);
            final PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            pw.close();
            return sw.toString();
        }
        catch (final Exception e) {
            return "";
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // start houndify listener
        startPhraseSpotting();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
        // if we don't, we must still be listening for "ok hound" so teardown the phrase spotter
        if ( phraseSpotterReader != null ) {
            stopPhraseSpotting();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // if we don't, we must still be listening for "ok hound" so teardown the phrase spotter
        if ( textToSpeechMgr != null ) {
            textToSpeechMgr.shutdown();
            textToSpeechMgr = null;
        }
    }

    public void handleShakeEvent(int count) {
        Log.d("SHAKE", "Shaked device: " + count + " times.");
        if (count <= 2) {
//            textToSpeechMgr.speak("Stop shaking me dude!");
            // TODO: modify speech to reflect randomly selected "popular" event
            textToSpeechMgr.speak("It looks like a lot of fun is going around.");
        }
        else {
            textToSpeechMgr.speak("Jeff, you are a very good looking man.");
        }
        ft = getFragmentManager().beginTransaction();
        testFragment newFragment = new testFragment();
        ft.add(R.id.embedded, newFragment);
        ft.commit();

        // TODO: Update popular event suggestion on shake
//        showDialog();
//        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
//        TrendingFragment dialogFragment = new TrendingFragment();
//        dialogFragment.show(fm, "Test Fragment");
    }

    void showDialog() {
        // Create the fragment and show it as a dialog.
        testFragment newFragment = new testFragment();
        newFragment.show(getFragmentManager(), "dialog");
    }


    /**
     * Helper class used for managing the TextToSpeech engine (from Houndify sample)
     */
    class TextToSpeechMgr implements TextToSpeech.OnInitListener {
        private TextToSpeech textToSpeech;

        public TextToSpeechMgr( Activity activity ) {
            textToSpeech = new TextToSpeech( activity, this );
        }

        @Override
        public void onInit( int status ) {
            // Set language to use for playing text
            if ( status == TextToSpeech.SUCCESS ) {
                int result = textToSpeech.setLanguage(Locale.US);
            }
        }

        public void shutdown() {
            textToSpeech.shutdown();
        }
        /**
         * Play the text to the device speaker
         *
         * @param textToSpeak
         */
        public void speak( String textToSpeak ) {
            textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_ADD, null);
        }
    }

}