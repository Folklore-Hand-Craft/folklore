package com.example.floklores.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Product;
import com.amplifyframework.datastore.generated.model.Section;

import com.example.floklores.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddProduct extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "AddProductActivity";

    private static final String IMAGE_URL_PREFIX = "https://folkloreb59a9082957b40879d7a28ed9441caea161824-dev.s3.amazonaws.com/public/";
    private static String productID;

    private final List<Section> sections = new ArrayList<>();

    // UPLOAD VARIABLES
    static String format = "dd-MM-yyyy-HH-mm-ss";
    @SuppressLint("SimpleDateFormat")
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    private static String uploadedFileName = simpleDateFormat.format(new Date());
    private static String uploadedFileExtension = null;
    private static File uploadFile = null;

    //    LOCATION VARIABLES
    private GoogleMap googleMap;
    private Location currentLocation;
    FusedLocationProviderClient fusedLocationClient;
    int PERMISSION_ID = 99;
    String addressString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        askForPermissionToUseLocation();
        configureLocationServices();
        askForLocation();
        setTitle("Add Product");
        getAllSectionsDataFromAPI();


        // SECTIONS SPINNER
        Spinner sectionsList = findViewById(R.id.spinnerSection);
        String[] sections = new String[]{"Pottery", "Accessories", "Crochet", "Glass Art", "Others"};
        ArrayAdapter<String> SectionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sections);
        sectionsList.setAdapter(SectionsAdapter);

        // UPLOAD FILE
        ImageView uploadFile1 = findViewById(R.id.upload_bt);
        uploadFile1.setOnClickListener(v1 -> getFileFromDevice());


        // ADD PRODUCT BUTTON HANDLER
        findViewById(R.id.buttonAddProduct).setOnClickListener(view -> {
            String productTitle = ((EditText) findViewById(R.id.editTextProductTitle)).getText().toString();
            String productBody = ((EditText) findViewById(R.id.editTextProductBody)).getText().toString();
            String productPrice = ((EditText) findViewById(R.id.editTextProductPrice)).getText().toString();
            String productContact = ((EditText) findViewById(R.id.editTextProductContact)).getText().toString();

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();
            preferenceEditor.putString("location", addressString);
            preferenceEditor.apply();
            String location = sharedPreferences.getString("location", "");

            Spinner sectionSpinner = (Spinner) findViewById(R.id.spinnerSection);
            String sectionName = sectionSpinner.getSelectedItem().toString();


            // get the Team from Database and it will update the teamId with the new id

            Log.i(TAG, "on button Listener the team id is >>>>> " + getSectionId(sectionName));

            // SAVE PRODUCT TO DynamoDB
            addProductToDynamoDB(productTitle,
                    productBody,
                    productPrice,
                    productContact,
                    new Section(getSectionId(sectionName), sectionName),
                    location
            );

            // GO HOME AFTER ADDING
            simpleDateFormat = new SimpleDateFormat(format);
            uploadedFileName = simpleDateFormat.format(new Date());
            uploadedFileExtension = null;
            uploadFile = null;
            Intent goToMainActivity = new Intent(AddProduct.this, MainActivity.class);
            startActivity(goToMainActivity);

        });

        //GO HOME PAGE
        ImageView goHomeImage = findViewById(R.id.fromAddProductActivityToHome);
        goHomeImage.setOnClickListener(v -> {

            Intent goHome = new Intent(AddProduct.this, MainActivity.class);
            startActivity(goHome);
        });
    }

    // METHODS

    public void addProductToDynamoDB(String productTitle, String productBody, String productPrice, String productContact, Section section, String location) {
        Product product = Product.builder()
                .productTitle(productTitle)
                .productBody(productBody)
                .productPrice(productPrice)
                .productContact(productContact)
                .section(section)
                .fileName( uploadedFileName +"."+ uploadedFileExtension.split("/")[1])
                .location(location)
                .build();

        Amplify.API.mutate(ModelMutation.create(product),
                success -> Log.i(TAG, "Saved item: " + product.getId()), // save this is some variable
                error -> Log.e(TAG, "Could not save item to API", error));
                productID = product.getId();

        Amplify.Storage.uploadFile(
                uploadedFileName +"."+ uploadedFileExtension.split("/")[1],
                uploadFile,
                success -> {
                    Log.i(TAG, "uploadFileToS3: succeeded " + success.getKey()); // the key is the file name of the image that was uploaded

                    // TODO: 2021-09-08 update the product you just uploaded to have the filename of the image uploaded

//                    IMAGE_URL_PREFIX + success.getKey();
//                    Amplify.API.mutate(ModelMutation.update(product),
//                             response ->
//                                    Log.i("MyAmplifyApp", "Updated profile with id: " + response.getData().getId()) ,
//
//                                    error -> Log.e("MyAmplifyApp", "Update failed", error)
//                    ); // update the product with the S3 storage file name
                },
                error -> {
                    Log.e(TAG, "uploadFileToS3: failed " + error.toString());
                }
        );

        Toast toast = Toast.makeText(this, "submitted!", Toast.LENGTH_LONG);
        toast.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 999 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            uploadedFileExtension = getContentResolver().getType(uri);

            Log.i(TAG, "onActivityResult: gg is " + uploadedFileExtension);
            Log.i(TAG, "onActivityResult: returned from file explorer");
            Log.i(TAG, "onActivityResult: => " + data.getData());
            Log.i(TAG, "onActivityResult:  data => " + data.getType());

            uploadFile = new File(getApplicationContext().getFilesDir(), "uploadFile");

            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                FileUtils.copy(inputStream, new FileOutputStream(uploadFile));
            } catch (Exception exception) {
                Log.e(TAG, "onActivityResult: file upload failed" + exception.toString());
            }

        }
    }

    private void getFileFromDevice() {
        Intent upload = new Intent(Intent.ACTION_GET_CONTENT);
        upload.setType("*/*");
        upload = Intent.createChooser(upload, "Choose a File");
        startActivityForResult(upload, 999); // deprecated
    }


    private void getAllSectionsDataFromAPI() {
        Amplify.API.query(ModelQuery.list(Section.class),
                response -> {
                    for (Section section : response.getData()) {
                        sections.add(section);
                        Log.i(TAG, "the section id DynamoDB are => " + section.getSectionName() + "  " + section.getId());
                    }
                },
                error -> Log.e(TAG, "onCreate: Failed to get section from DynamoDB => " + error.toString())
        );
    }


    public String getSectionId(String sectionName) {
        for (Section section : sections) {
            if (section.getSectionName().equals(sectionName)) {
                return section.getId();
            }
        }
        return "";
    }

    //    location

    private void configureLocationServices() {
        fusedLocationClient  = LocationServices.getFusedLocationProviderClient(this);

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void askForPermissionToUseLocation(){
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 2);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

    }
    @SuppressLint("MissingPermission")
    public void askForLocation() {

        LocationRequest locationRequest;
        LocationCallback locationCallback;

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                currentLocation = locationResult.getLastLocation();
                Log.i("Location", currentLocation.toString());

                Geocoder geocoder = new Geocoder(AddProduct.this, Locale.getDefault());

                try {
                    List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 10);
                    addressString = addresses.get(0).getAddressLine(0);
                    Log.e("Location","Your Address is " + " StringAddress" + addressString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
    }

}