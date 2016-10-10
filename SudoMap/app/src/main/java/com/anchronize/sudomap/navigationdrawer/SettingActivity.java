package com.anchronize.sudomap.navigationdrawer;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.anchronize.sudomap.R;
import com.anchronize.sudomap.SudoMapApplication;
import com.anchronize.sudomap.objects.User;
import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class SettingActivity extends AppCompatActivity {
    private EditText nameEditText;
    private Button saveButton;
    private ImageView myImageView;
    private final int SELECT_PHOTO = 1;
    private FloatingActionButton myFab;
    private Bitmap selectedImage = null;

    private Firebase ref, refUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initializeComponents();
        addListeners();
        popoulateFields();
    }

    public void initializeComponents(){
        nameEditText = (EditText) findViewById(R.id.name_editText);
        myFab = (FloatingActionButton) findViewById(R.id.change_pic_button);
        myImageView = (ImageView) findViewById(R.id.profile_pic);
        saveButton = (Button) findViewById(R.id.saveButton);
        ref = new Firebase("https://anchronize.firebaseio.com");
    }

    public void addListeners(){
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImageToFB();
            }
        });
    }

    public void popoulateFields(){
        User current = ((SudoMapApplication)getApplication()).getCurrentUser();
        nameEditText.setText(current.getInAppName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == RESULT_OK) {
            try {
                Uri imageUri = imageReturnedIntent.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                changePicOrientation(imageUri, selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    // Called after choosing a picture
    public void changePicOrientation(Uri imageUri, Bitmap selectedImage){
        String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
        Cursor cur = getContentResolver().query(imageUri, orientationColumn, null, null, null);
        int orientation = -1;
        if (cur != null && cur.moveToFirst()) {
            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        selectedImage = Bitmap.createBitmap(selectedImage, 0, 0, selectedImage.getWidth(),
                selectedImage.getHeight(), matrix, true);
        myImageView.getLayoutParams().height = myImageView.getMeasuredHeight();
        myImageView.getLayoutParams().width = myImageView.getMeasuredWidth();
        myImageView.setImageBitmap(selectedImage);
    }

    //Called when picture is clicked
    public void onClickPic(View view) {
        if(selectedImage == null){
            return;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        selectedImage = Bitmap.createBitmap(selectedImage, 0, 0, selectedImage.getWidth(),
                selectedImage.getHeight(), matrix, true);
        myImageView.setImageBitmap(selectedImage);

    }

    public void saveImageToFB(){
        if(selectedImage == null){
            Log.d("save", "trying to say hi");
            finish();
            return;
        }
        else {
            // Converting to string to push to firebase
            Bitmap copySelectedImage = getResizedBitmap(selectedImage, 500);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            copySelectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String imgToSave = Base64.encodeToString(b, Base64.DEFAULT);
            String currentUserID = ((SudoMapApplication) getApplication()).getCurrentUserID();
            refUser = ref.child("users").child(currentUserID);
            Map<String, Object> profileIMGStringMap = new HashMap<String, Object>();
            profileIMGStringMap.put("profileImgString", imgToSave);
            refUser.updateChildren(profileIMGStringMap);
            System.out.println("selectedImage is null");
            finish();
            return;
        }
    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}