package com.sp.vending;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DetailForm extends AppCompatActivity {
    private EditText restaurantName;
    private EditText restaurantAddress;
    private EditText restaurantTel;
    private ImageView imageView;
    private Bitmap imageBitmap;
    private Button imageUpload;
    private Button imageUploadCamera;
    private Button buttonSave;
    private byte[] bytes;

    private assignmentsql helper = null;
    private String  restaurantID = "";

    private TextView location = null;
    private GPSTracker gpsTracker;
    private double latitude = 0.0d;
    private double longitude = 0.0d;
    private double myLatitude = 0.0d;
    private double myLongitude = 0.0d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_form);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        restaurantName = findViewById(R.id.restaurant_name);
        restaurantAddress = findViewById(R.id.restaurant_address);
        restaurantTel = findViewById(R.id.restaurant_tel);

        buttonSave = findViewById(R.id.button_save);
        buttonSave.setOnClickListener(onSave);
        imageView = findViewById(R.id.image_view);
        imageUploadCamera = findViewById(R.id.uploadCamera);
        imageUploadCamera.setOnClickListener(onUploadCamera);

        helper = new assignmentsql(this);

        location = findViewById(R.id.location);
        gpsTracker = new GPSTracker(DetailForm.this);

        restaurantID = getIntent().getStringExtra("ID");
        if (restaurantID != null) {
            load();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.close();
        gpsTracker.stopUsingGPS();
    }

    private void load() {
        Cursor c = helper.getById(restaurantID);
        c.moveToFirst();
        restaurantName.setText(helper.getRestaurantName(c));
        restaurantAddress.setText(helper.getRestaurantAddress(c));
        restaurantTel.setText(helper.getRestaurantTel(c));
        byte[] bytesImage = helper.getImage(c);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytesImage, 0, bytesImage.length);
        imageView.setImageBitmap(bitmap);
        imageBitmap = bitmap;
        latitude = helper.getLatitude(c);
        longitude = helper.getLongitude(c);


        location.setText(String.valueOf(latitude) + ", " + String.valueOf(longitude));
    }



    @Override
    public  boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.details_option, menu);
        return  super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.get_location) {
            if (gpsTracker.canGetLocation()) {

                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
                location.setText(String.valueOf(latitude) + ", " + String.valueOf(longitude));

                Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            }
            return (true);
        } else if (item.getItemId() == R.id.show_map) {
            myLatitude = gpsTracker.getLatitude();
            myLongitude = gpsTracker.getLongitude();

            Intent  intent = new Intent(this, vendingMap.class);
            intent.putExtra("LATITUDE", latitude);
            intent.putExtra("LONGITUDE", longitude);
            intent.putExtra("MYLATITUDE", myLatitude);
            intent.putExtra("MYLONGITUDE", myLongitude);
            intent.putExtra("NAME", restaurantName.getText().toString());
            startActivity(intent);
            return (true);
        }
        return super.onOptionsItemSelected(item);
    }

    View.OnClickListener onUploadCamera = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          AlertDialog.Builder builder = new AlertDialog.Builder(DetailForm.this);
          builder.setMessage("Upload from gallery or camera");
          builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                  try {
                      Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                      startActivityForResult(takePicture, 0);
                  } catch (ActivityNotFoundException e) {
                      Toast.makeText(DetailForm.this, "You haven't picked a Image",Toast.LENGTH_LONG).show();
                  }
              }
          });
          builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                  Intent takePictureIntent = new Intent(Intent.ACTION_PICK,
                          android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                  try {
                      startActivityForResult(takePictureIntent, 1);
                  } catch (ActivityNotFoundException e) {
                      // display error state to the user
                      Toast.makeText(DetailForm.this, "You haven't picked a Image",Toast.LENGTH_LONG).show();
                  }
              }
          });
          builder.setIcon(android.R.drawable.ic_dialog_alert);
          builder.show();
      }

    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    imageBitmap = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    imageView.setImageBitmap(imageBitmap);
                    byte[] byteArray = stream.toByteArray();
                    bytes = byteArray;
                }
                break;

            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();


                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageView.setImageBitmap(imageBitmap);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    bytes = byteArray;
                }
                break;
        }

    }




    View.OnClickListener onSave = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            //To read data from restaurantName EditText
            String nameStr = restaurantName.getText().toString();
            String addressStr = restaurantAddress.getText().toString();
            String telStr = restaurantTel.getText().toString();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytesImage = stream.toByteArray();


            if(restaurantID == null) {
                helper.insert(nameStr, addressStr, telStr, latitude, longitude, bytesImage);
            } else{
                helper.update(restaurantID, nameStr, addressStr, telStr, latitude, longitude, bytesImage);
            }
            finish();
        }
    };
}
