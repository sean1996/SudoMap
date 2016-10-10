package com.anchronize.sudomap;

/*
 * Copyright 2015 Rudson Lima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.internal.ScrimInsetsFrameLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anchronize.sudomap.objects.Event;
import com.anchronize.sudomap.objects.ShakeDetector;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.hound.android.libphs.PhraseSpotterReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.liveo.adapter.NavigationLiveoAdapter;
import br.liveo.interfaces.OnItemClickListener;
import br.liveo.interfaces.OnPrepareOptionsMenuLiveo;
import br.liveo.model.HelpItem;
import br.liveo.model.Navigation;

public abstract class NavigationLiveo extends AppCompatActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowCloseListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    public TextView userName;
    public TextView userEmail;
    public ImageView userPhoto;
    public ImageView userBackground;

    protected View mHeader;

    protected ListView mList;
    protected Toolbar mToolbar;
    protected TextView mTitleFooter;
    protected ImageView mIconFooter;

    protected TextView mTitleSecondFooter;
    protected ImageView mIconSecondFooter;

    protected int mColorName = 0;
    protected int mColorIcon = 0;
    protected int mNewSelector = 0;
    protected int mColorCounter = 0;
    protected int mColorSeparator = 0;
    protected int mColorSubHeader = 0;
    protected boolean mRemoveHeader = false;
    protected boolean mCustomHeader = false;

    protected int mColorDefault = 0;
    protected int mCurrentPosition = 1;
    protected int mCurrentCheckPosition = 1;
    protected int mSelectorDefault = 0;
    protected float mElevationToolBar = 15;
    protected boolean mRemoveAlpha = false;
    protected boolean mRemoveColorFilter = false;

    protected List<HelpItem> mHelpItem;
    protected RelativeLayout mFooterDrawer;
    protected RelativeLayout mFooterSecondDrawer;
    protected ScrimInsetsFrameLayout mRelativeDrawer;

    protected boolean isSaveInstance = false;
    protected Navigation mNavigation = new Navigation();

    protected NavigationLiveoAdapter mNavigationAdapter;
    protected HomeActivity.ActionBarDrawerToggleCompat mDrawerToggle;

    protected OnItemClickListener mOnItemClickLiveo;
    protected OnPrepareOptionsMenuLiveo mOnPrepareOptionsMenu;

    public static final String CURRENT_POSITION = "CURRENT_POSITION";
    public static final String CURRENT_CHECK_POSITION = "CURRENT_CHEKC_POSITION";


    // Google Map
    protected static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    protected boolean mPermissionDenied = false;
    protected GoogleMap mMap;
    protected Marker lastSelectedMarker;

    // Firebase API variables
    protected Firebase ref;
    protected ArrayList<Event> allEventsinFirebase;   //store all events in database
    protected HashMap<Marker, Event> markerEventHashMap; //maintain a map from maker to event
    protected ArrayList<Event> allEventsToDisplay;  //store all events to be displayed
    protected String selectedFilter = "ALL"; //Default the selectedFilter to all events

    // Searchview
    private final String TAG = "HomeActivity";
    protected FloatingSearchView mSearchView;
    protected DrawerLayout mDrawerLayout;
    protected FloatingActionButton mAddEventButton;

    // shake detection API
    protected SensorManager mSensorManager;
    protected Sensor mAccelerometer;
    protected ShakeDetector mShakeDetector;

    // Houndify
    protected PhraseSpotterReader phraseSpotterReader;
    protected Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    HomeActivity.TextToSpeechMgr textToSpeechMgr;


    /**
     * onCreate(Bundle savedInstanceState).
     * @param savedInstanceState onCreate(Bundle savedInstanceState).
     */
    public abstract void onInt(Bundle savedInstanceState);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Implementation of the PhraseSpotterReader.Listener interface used to handle PhraseSpotter
     * call back.
     */
//    protected final PhraseSpotterReader.Listener phraseSpotterListener = new PhraseSpotterReader.Listener() {
//        @Override
//        public void onPhraseSpotted() {
//
//            // It's important to note that when the phrase spotter detects "Ok Hound" it closes
//            // the input stream it was provided.
//            mainThreadHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    stopPhraseSpotting();
//                    // Now start the HoundifyVoiceSearchActivity to begin the search.
//                    Houndify.get( NavigationLiveo.this ).voiceSearch( NavigationLiveo.this );
//                }
//            });
//        }
//
//        @Override
//        public void onError(final Exception ex) {
//
//            // for this sample we don't care about errors from the "Ok Hound" phrase spotter.
//
//        }
//    };
    protected void mountListNavigation(Bundle savedInstanceState){
        if (mOnItemClickLiveo == null){
            this.createUserDefaultHeader();
            this.onInt(savedInstanceState);
        }
    }

    /**
     * Create user default header
     */
    protected void createUserDefaultHeader() {
        mHeader = getLayoutInflater().inflate(br.liveo.navigationliveo.R.layout.navigation_list_header, mList, false);

        userName = (TextView) mHeader.findViewById(br.liveo.navigationliveo.R.id.userName);
        userEmail = (TextView) mHeader.findViewById(br.liveo.navigationliveo.R.id.userEmail);
        userPhoto = (ImageView) mHeader.findViewById(br.liveo.navigationliveo.R.id.userPhoto);
        userBackground = (ImageView) mHeader.findViewById(br.liveo.navigationliveo.R.id.userBackground);
    }

    public abstract NavigationLiveo footerItem(String title, int icon);
    public abstract NavigationLiveo footerItem(int title, int icon);
    public abstract NavigationLiveo setOnClickUser(View.OnClickListener listener);
    public abstract NavigationLiveo setOnPrepareOptionsMenu(OnPrepareOptionsMenuLiveo onPrepareOptionsMenu);
    public abstract NavigationLiveo setOnClickFooter(View.OnClickListener listener);
    public abstract NavigationLiveo setOnClickFooterSecond(View.OnClickListener listener);
    public abstract NavigationLiveo footerSecondItem(String title, int icon);
    public abstract NavigationLiveo addAllHelpItem(List<HelpItem> listHelpItem);
    public abstract NavigationLiveo startingPosition(int position);
    public abstract void build();

}