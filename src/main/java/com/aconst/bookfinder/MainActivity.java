package com.aconst.bookfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        String query = ((EditText) findViewById(R.id.query)).getText().toString();
        if (!query.isEmpty()) {
            query = query.replace(" ", "+");

            Intent intent = new Intent(this, ActivityBookList.class);
            intent.putExtra("query", query);
            startActivity(intent);
        }
    }
}
