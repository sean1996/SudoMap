package com.anchronize.sudomap.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by tianlinz on 3/25/16.
 */
public class Event implements Serializable {

    public static final long serialVersionUID = 1L;

    public Event(){

    }

    public Event(String ID){
        eventID = ID;
    }

    public void setEventID(String eventID){
        this.eventID = eventID;
    }

    public String getEventID(){
        return eventID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOrganizerID() {
        return organizerID;
    }

    public void setOrganizerID(String organizerID) {
        this.organizerID = organizerID;
    }

    public ArrayList<String> getattendantsID() {
        return attendantsID;
    }

    public void addAttendant(String ai) {
        this.attendantsID.add(ai);
    }

    public void removeAttendant(String attendantID){
        for(String id: attendantsID){
            if(id.equals(attendantID)){
                attendantsID.remove(id);
            }
        }
    }


    public boolean getPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public HashMap<Integer, Integer> getnumOfPostsAtTime() {
        return numOfPostsAtTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getcreationTime() {
        return creationTime;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartDay() {
        return startDay;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndDay() {
        return endDay;
    }

    public void setEndDay(int endDay) {
        this.endDay = endDay;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(int endMonth) {
        this.endMonth = endMonth;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public String formattedDateString(){
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, startHour);
        time.set(Calendar.MINUTE, startMinute);
        String startAM_PM = "";

        if(time.get(Calendar.AM_PM) == Calendar.AM)
            startAM_PM = "AM";
        else if(time.get(Calendar.AM_PM) == Calendar.PM)
            startAM_PM = "PM";

        String hour = (time.get(Calendar.HOUR) == 0) ?"12":time.get(Calendar.HOUR)+"";
        int startHour = Integer.parseInt(hour);

        int selectedMinute = time.get(Calendar.MINUTE);
        int startMinute = selectedMinute;
        String sMinute = "00";
        if(selectedMinute < 10){
            sMinute = "0" + selectedMinute;
        } else {
            sMinute = selectedMinute + "";
        }

        String timeFirstPart = hour + ":" + sMinute + " " + startAM_PM;


        int startYearNew = startYear;
        int startMonthNew = startMonth;
        int startDayNew = startDay;

        time.set(Calendar.MONTH, startMonthNew);
        String month = time.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
        String date = month + " " + startDayNew + ", " + startYearNew;
        String toBeRETURN = date + " at " + timeFirstPart;
        return toBeRETURN;
    }

    private String eventID;
    private String title;
    private String description;
    private String category;
    private String organizerID;
    private ArrayList<String> attendantsID = new ArrayList<String>();
    private boolean privacy;
    private String address;
    private String addressName;
    private double longitude;
    private double latitude;
    private boolean visible;
    private HashMap<Integer, Integer> numOfPostsAtTime = new HashMap<Integer, Integer>();
    private String creationTime;
    private int startMinute;
    private int startHour;
    private int startDay;
    private int startMonth;
    private int startYear;
    private int endMinute;
    private int endHour;
    private int endDay;
    private int endMonth;
    private int endYear;
}
