package com.example.floklores.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.floklores.R;

public class Details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTitle("Details");

        // handling the Data passed via the intent
        Intent intent = getIntent();

        String productName = intent.getExtras().getString("productTitle");
        String productBody = intent.getExtras().getString("productBody");
        String productContact = intent.getExtras().getString("productContact");
        String productPrice = intent.getExtras().getString("productPrice");

        ((TextView)findViewById(R.id.textViewDetailsProductTitle)).setText(productName);
        ((TextView)findViewById(R.id.productBody)).setText(productBody);
        ((TextView)findViewById(R.id.productContact)).setText(productContact);
        ((TextView)findViewById(R.id.productPrice)).setText(productPrice);


        //Go Home!
        ImageView goHomeImage = findViewById(R.id.fromTaskDetailsActivityToHome);
        goHomeImage.setOnClickListener(v -> {
            // back button pressed
            Intent goHome = new Intent(Details.this,MainActivity.class);
            startActivity(goHome);
        });

    }
}