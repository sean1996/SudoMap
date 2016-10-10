package com.anchronize.sudomap;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by tianlinz on 4/16/16.
 */

public class AttendantsItem extends LinearLayout {

    private TextView nameView;
    private ImageView picView;
    private View myView;

    private String name;
    private Bitmap picBitMap;

    public AttendantsItem(Context context) {
        super(context);
        init(context);
    }

    public AttendantsItem(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);

    }

    public AttendantsItem(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater;
        inflater = (LayoutInflater) context .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myView = inflater.inflate(R.layout.attendants_item, this);

        nameView = (TextView) myView.findViewById(R.id.nameView);
        picView = (ImageView) myView.findViewById(R.id.picView);
    }

    public void setName(String n){
        name = n;
        nameView.setText(name);
    }

    public void setPicBitMap(Bitmap p){
        picBitMap = p;
        picView.setImageBitmap(p);
    }
}
