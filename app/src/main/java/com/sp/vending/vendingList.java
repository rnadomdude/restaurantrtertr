package com.sp.vending;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import android.content.Context;
import androidx.cursoradapter.widget.CursorAdapter;

import java.lang.reflect.Field;

public class vendingList extends AppCompatActivity {
    private Cursor model = null;
    private RestaurantAdapter adapter = null;
    private ListView list;
    private assignmentsql helper = null;
    private TextView empty = null;
    private GPSTracker gpsTracker;
    private String restaurantName = "";
    private double latitude = 0.0d;
    private double longitude = 0.0d;
    private double myLatitude = 0.0d;
    private double myLongitude = 0.0d;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //set the max image size to 100mb for better quality in the cursor
        } catch (Exception e) {
                e.printStackTrace();
        }

        empty = findViewById(R.id.empty);
        helper = new assignmentsql(this);
        list = findViewById(R.id.list);
        model = helper.getAll();
        adapter = new RestaurantAdapter(this, model, 0);
        list.setOnItemClickListener(onListClick);
        list.setAdapter(adapter);
        helper = new assignmentsql(this);
        gpsTracker = new GPSTracker(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (model != null) {
            model.close();
        }
        model = helper.getAll();
        if (model.getCount()>0) {
            empty.setVisibility(ViewGroup.INVISIBLE);
        }
        adapter.swapCursor(model);
    }

    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
        helper.close();
        gpsTracker.stopUsingGPS();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.add):
                Intent intent;
                intent = new Intent(vendingList.this, DetailForm.class);
                startActivity(intent);
                break;
            case (R.id.about):
                intent  = new Intent(vendingList.this, aboutpage.class);
                startActivity(intent);
                break;
            case (R.id.exit):
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(vendingList.this);
                    builder1.setTitle("Edit or view map");
                    builder1.setMessage("Edit or view map");
                    builder1.setPositiveButton("Open Map", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            myLatitude = gpsTracker.getLatitude();
                            myLongitude = gpsTracker.getLongitude();

                            Intent  intent = new Intent(vendingList.this, vendingMap.class);
                            intent.putExtra("LATITUDE", latitude);
                            intent.putExtra("LONGITUDE", longitude);
                            intent.putExtra("MYLATITUDE", myLatitude);
                            intent.putExtra("MYLONGITUDE", myLongitude);
                            intent.putExtra("NAME", restaurantName);
                            startActivity(intent);
                        }
                    })

                    .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                model.moveToPosition(position);
                                String recordID = helper.getID(model);
                                Intent intent;
                                intent = new  Intent(vendingList.this, DetailForm.class);
                                intent.putExtra("ID", recordID);
                                startActivity(intent);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();


        }
    };

    class RestaurantHolder {
        private TextView restName = null;
        private TextView addr = null;
        private ImageView icon = null;

        RestaurantHolder(View row) {
            restName = row.findViewById(R.id.restName);
            addr = row.findViewById(R.id.restAddr);
            icon = row.findViewById(R.id.icon);
        }
        void populateFrom(Cursor c, assignmentsql helper) {
            restName.setText(helper.getRestaurantName(c));
            String name = restName.getText().toString();
            double lat = helper.getLatitude(c);
            double longi = helper.getLongitude(c);
            latitude = lat;
            longitude = longi;
            restaurantName = name;

            String temp = helper.getRestaurantAddress(c) + ", " + helper.getRestaurantTel(c);
            byte[] bytesImage = helper.getImage(c);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytesImage, 0, bytesImage.length);
            icon.setImageBitmap(bitmap);
            addr.setText(temp);
        }

    }

    class RestaurantAdapter extends CursorAdapter {
        RestaurantAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            RestaurantHolder holder = (RestaurantHolder) view.getTag();
            holder.populateFrom(cursor, helper);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row, parent, false);
            RestaurantHolder holder = new RestaurantHolder(row);
            row.setTag(holder);
            return (row);
        }
    }
}
