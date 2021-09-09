package com.example.floklores.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;
import com.bumptech.glide.Glide;
import com.example.floklores.R;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Details extends AppCompatActivity {

    private static final String TAG = "ProductDetail";
    private URL url =null;
    private Handler handler;
    private char [] fileChar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTitle("Product Details");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String location = sharedPreferences.getString("location", "location");

        ((TextView)findViewById(R.id.location)).setText(location);

        //HANDLING THE DATA PASSED VIA INTENT
        Intent intent = getIntent();

        String productName = intent.getExtras().getString("productTitle");
        String productBody = intent.getExtras().getString("productBody");
        String productContact = intent.getExtras().getString("productContact");
        String productPrice = intent.getExtras().getString("productPrice");

        ((TextView)findViewById(R.id.textViewDetailsProductTitle)).setText(productName);
        ((TextView)findViewById(R.id.productBody)).setText(productBody);
        ((TextView)findViewById(R.id.productContact)).setText(productContact);
        ((TextView)findViewById(R.id.productPrice)).setText(productPrice);


//-------------------------
//        SharedPreferences sharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        SharedPreferences.Editor preferenceEditor2 = sharedPreferences2.edit();
//
        String fileName = intent.getExtras().getString("productFile");
        List<Character> FileNameCharOnly = new ArrayList<>();
        char ch[]=fileName.toCharArray();
        String rev="";
        for(int i=ch.length-1;i>=0;i--){
            rev+=ch[i];
        }

        System.out.println("reeeeeeev1 ---> " + rev);

        char ch2[]=rev.toCharArray();

        for(int i=0; i<rev.length(); i++){

            if(rev.charAt(i) != '/'){
                FileNameCharOnly.add(rev.charAt(i));
            }else if(rev.charAt(i) == '/'){
                break;
            }
        }

        System.out.println("reeeeeeev2 ---> "+ rev);

        char ch3[]=rev.toCharArray();
        String finalFileName="";
        for(int i=FileNameCharOnly.size()-1;i>=0;i--){
            finalFileName+= FileNameCharOnly.get(i);
        }

        System.out.println("finaaaaaaaaaaaal name ---> " + finalFileName);




//        preferenceEditor2.putString("file",fileName);
//        preferenceEditor2.apply();
//--------------------------

        ImageView imageView = findViewById(R.id.product_img);

        handler = new Handler(Looper.getMainLooper(),
                message -> {
                    Glide.with(getBaseContext())
                            .load(url.toString())
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .centerCrop()
                            .into(imageView);
                    return true;
                });
        System.out.println("fillllllllllllllle naaaaaaaaaaaaaaame" + fileName);

        getFileFromS3Storage(finalFileName);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String fileLink = String.format("<a href=\"%s\">download File</a> ", url);

        TextView link = findViewById(R.id.product_link);
        link.setText(Html.fromHtml(fileLink));
        link.setMovementMethod(LinkMovementMethod.getInstance());

    }

    // METHODS

    private void getFileFromS3Storage(String key) {
        Amplify.Storage.downloadFile(
                key,
                new File(getApplicationContext().getFilesDir() + key),
                result -> {
                    Log.i(TAG, "Successfully downloaded: " + result.getFile().getAbsoluteFile());
                },
                error -> Log.e(TAG,  "Download Failure", error)
        );

        Amplify.Storage.getUrl(
                key,
                result -> {
                    Log.i(TAG, "Successfully generated: " + result.getUrl());
                    url= result.getUrl();
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e(TAG, "URL generation failure", error)
        );


        //GO HOME PAGE
        ImageView goHomeImage = findViewById(R.id.fromTaskDetailsActivityToHome);
        goHomeImage.setOnClickListener(v -> {
            Intent goHome = new Intent(Details.this,MainActivity.class);
            startActivity(goHome);
        });

    }
}