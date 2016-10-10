package com.anchronize.sudomap;

import android.util.Log;

import com.anchronize.sudomap.objects.User;
import com.facebook.FacebookSdk;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.hound.android.fd.Houndify;

/**
 * Created by tianlinz on 4/14/16.
 */
public class SudoMapApplication extends android.app.Application{

    private boolean isAuthenticated;
    private User currentUser;
    private String currentUserID;

    private Firebase ref, refCurrentUser;
    private String currentUsername;


    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        Firebase.setAndroidContext(this);
        ref =  new Firebase("https://anchronize.firebaseio.com");
        isAuthenticated = false;    //default it to false when it's created first
        currentUserID = null;



        Houndify.get(this).setClientId( Constants.CLIENT_ID );
        Houndify.get(this).setClientKey( Constants.CLIENT_KEY );
        Houndify.get(this).setRequestInfoFactory(StatefulRequestInfoFactory.get(this));
    }

    public void setAuthenticateStatus(boolean authStatus) {
        this.isAuthenticated = authStatus;
    }

    public boolean getAuthenticateStatus() {
        return isAuthenticated;
    }


    public void updateCurrentUser(final User user){
//        if(!user.getUserID().equalsIgnoreCase(currentUserID)){
//
//            return;
//        }
//
//        Query queryRef = refUsers.orderByChild("userID").equalTo(user.getUserID());
//        Log.d("progress", "here");
//        queryRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
//                //get the nameOfSubTree
//                String subTreename = snapshot.getKey();
//                Log.d("subTree", subTreename);
//                Firebase refSubTree = refUsers.child(subTreename);
//                Log.d("bio", user.getUserBio());
//                //update the user on the Firebase
//                refSubTree.setValue(user);
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//
//        });

    }

    //When other activity class call this methods, the application will listen to firebase of currentUser
    //and store the up-to-date version of the current user local
    public void StartToUpdateUser(){
        if(currentUserID != null){
            refCurrentUser = ref.child("users").child(currentUserID);
            refCurrentUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentUser = dataSnapshot.getValue(User.class);
                    currentUsername = currentUser.getInAppName();
                    Log.d("currentUsername", currentUsername);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    public User getCurrentUser() {

        return currentUser;
    }

    public String getCurrentUsername() {

        return currentUsername;
    }

    public void setCurrentUserID(String currentID) {
        this.currentUserID = currentID;
    }

    public String getCurrentUserID() {
        return currentUserID;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

}
