package com.anchronize.sudomap.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.anchronize.sudomap.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by tianlinz on 4/2/16.
 */
public class User implements Serializable {

    public static final long serialVersionUID = 2L;

    public User(){}

    public User(String ID){
        userID = ID;
    }

    public String getUserID() {
        return userID;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public String getInAppName() {
        return inAppName;
    }

    public void setInAppName(String inAppName) {
        this.inAppName = inAppName;
    }

    public String getProfileImgString() {
        return profileImgString;
    }

    public void setProfileImgString(String profileImgString) {
        this.profileImgString = profileImgString;
    }

    public Bitmap getProfileImageBitMap(){
        byte [] encodeByte =Base64.decode(profileImgString,Base64.DEFAULT);
        Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        return bitmap;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public ArrayList<String> getAttendingEventIDs() {
        return attendingEventIDs;
    }

    public void addAttendingEventID(String aEI){
        this.attendingEventIDs.add(aEI);
    }

    public void removeAttendingEvent(String eventID){
        for(String ei: attendingEventIDs){
            if(ei.equals(eventID)){
                attendingEventIDs.remove(ei);
            }
        }
    }

    public ArrayList<String> getBookmarkedEventIDs() {
        return bookmarkedEventIDs;
    }

    public void addBookmarkedEventID(String bEI) {
        this.bookmarkedEventIDs.add(bEI);
    }

    public void removeBookmarkedEvent(String eventID){
        for(String ei: bookmarkedEventIDs){
            if(ei.equals(eventID)){
                bookmarkedEventIDs.remove(ei);
            }
        }
    }

    private boolean premium;
    private String userID;
    private String inAppName;
    private String profileImgString = "default";
    private String userBio;
    private ArrayList<String> attendingEventIDs = new ArrayList<String>();
    private ArrayList<String> bookmarkedEventIDs = new ArrayList<String>();
}

