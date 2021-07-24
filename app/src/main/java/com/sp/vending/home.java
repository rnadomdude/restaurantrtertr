package com.sp.vending;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

public class home extends AppCompatActivity {
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

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
                intent = new Intent(home.this, DetailForm.class);
                startActivity(intent);
                break;
            case (R.id.about):
                intent  = new Intent(home.this, aboutpage.class);
                startActivity(intent);
                break;
            case (R.id.viewtext):
                intent = new Intent(home.this, vendingList.class);
                startActivity(intent);
                break;
            case (R.id.exit):
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
