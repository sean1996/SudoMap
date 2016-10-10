package com.anchronize.sudomap;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anchronize.sudomap.objects.User;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Rohan on 4/13/16.
 */

public class ChatActivity extends ListActivity {

    private ArrayList<String> data = new ArrayList<String>();
    private static final String FIREBASE_URL = "https://anchronize.firebaseio.com";
    public static final String EVENTID_KEY = "EVENT ID KEY";
    public static final String EVENTDESC_KEY = "EVENT DESCRIPTION KEY";
    public static final String USERNAME_KEY = "USERNAME KEY";

    private String mUsername;
    private String mDescription;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private ChatListAdapter mChatListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_message);
        //get the eventID
        Intent i = getIntent();
        String eventID =  i.getStringExtra(EVENTID_KEY);
        mDescription = i.getStringExtra(EVENTDESC_KEY);
        mUsername = i.getStringExtra(USERNAME_KEY);

        //Set up firebase reference
        //this should be changed to be under each event
        mFirebaseRef = new Firebase(FIREBASE_URL).child("chat").child(eventID);

        //main post is replaced by description
        TextView description = (TextView) findViewById(R.id.main_post);
        description.setText(mDescription);

        //set up input text field and send message button listener
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });


        //add listadapter to listview
        final ListView listView = getListView();
        // Tell our list adapter that we only want 50 messages at a time
        mChatListAdapter = new ChatListAdapter(mFirebaseRef.limit(50), this, R.layout.list_item, mUsername);
        listView.setAdapter(mChatListAdapter);
        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });


    }

    private void sendMessage() {
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            // Create our 'model', a Chat object
            SimpleDateFormat sdf = new SimpleDateFormat("HH");
            String hour = sdf.format(new Date());
            Log.d("timestamp",hour);
            Chat chat = new Chat(input, mUsername);
            User user = ((SudoMapApplication)getApplication()).getCurrentUser();
            chat.setProfilePicture(user.getProfileImgString());
            chat.setHour(hour);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            Firebase mPostRef = mFirebaseRef.push();
            //mChatListAdapter.setReference(mPostRef.child("votes"));
            mPostRef.setValue(chat);
            inputText.setText("");
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
