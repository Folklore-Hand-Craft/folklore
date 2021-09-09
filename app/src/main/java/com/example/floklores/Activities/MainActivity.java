package com.example.floklores.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Product;
import com.amplifyframework.datastore.generated.model.Section;
import com.example.floklores.Adapter.ProductAdapter;
import com.example.floklores.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity  {
    private static final String TAG = "MainActivity";

    private List<Product> products;
    private ProductAdapter adapter;

    private Handler handler;
    String[] sections = new String[]{"All Sections","Pottery", "Accessories", "Crochet", "Glass Art", "Others"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Folklore");



        handler = new Handler(message -> {
            notifyDataSetChanged();
            return false;
        });


        Spinner sectionsList = findViewById(R.id.spinnerSection1);

        ArrayAdapter<String> SectionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sections);
        sectionsList.setAdapter(SectionsAdapter);

        findViewById(R.id.apply).setOnClickListener(view -> {
            SharedPreferences sharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor preferenceEditor2 = sharedPreferences2.edit();
            Spinner sectionSpinner = (Spinner) findViewById(R.id.spinnerSection1);
            String sectionName = sectionSpinner.getSelectedItem().toString();
            preferenceEditor2.putString("sectionName", sectionName);
            preferenceEditor2.apply();

            Intent goToAddTaskActivity = new Intent(MainActivity.this, MainActivity.class);
            startActivity(goToAddTaskActivity);
            finish();

        });


        // Go to add product activity

        findViewById(R.id.buttonAddProductActivity).setOnClickListener(view -> {
            Intent goToAddProductActivity = new Intent(MainActivity.this, AddProduct.class);
            startActivity(goToAddProductActivity);
        });


        // save Teams to API
        saveTeamToApi("Pottery");
        saveTeamToApi("Accessories");
        saveTeamToApi("Crochet");
        saveTeamToApi("Glass Art");
        saveTeamToApi("Others");

    }

    private void listItemDeleted() {
        adapter.notifyDataSetChanged();
    }



    public void showDetails(String productName) {
        Intent intent = new Intent(MainActivity.this, Details.class);
        intent.putExtra("productName", productName);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        //create sharedPreference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = sharedPreferences.getString("username", "");
        String sectionName = sharedPreferences.getString("sectionName", "");

        products = new ArrayList<>();
        if (sectionName.equals("All Sections")) {
            getTasksDataFromAPI();
        } else {
            getTeamTasksFromAPI(sectionName);
        }

        Log.i(TAG, "onResume: tasks " + products);

        RecyclerView taskRecyclerView = findViewById(R.id.list);
        adapter = new ProductAdapter(products, new ProductAdapter.OnTaskItemClickListener() {



            @Override
            public void onItemClicked(int position) {
                Intent goToDetailsIntent = new Intent(getApplicationContext(), Details.class);
                goToDetailsIntent.putExtra("productTitle", products.get(position).getProductTitle());
                goToDetailsIntent.putExtra("productBody", products.get(position).getProductBody());
                goToDetailsIntent.putExtra("productPrice", products.get(position).getProductPrice());
                goToDetailsIntent.putExtra("productContact", products.get(position).getProductContact());
                goToDetailsIntent.putExtra("productFile", products.get(position).getFileName());
                goToDetailsIntent.putExtra("location", products.get(position).getLocation());
                startActivity(goToDetailsIntent);
            }

            @Override
            public void onDeleteItem(int position) {

                Section section = Section.builder().sectionName("Team A").build();

                // todo: delete from Amplify API
                Product product = Product.builder()
                        .productTitle(products.get(position).getProductTitle())
                        .productBody(products.get(position).getProductBody())
                        .productPrice(products.get(position).getProductPrice())
                        .productContact(products.get(position).getProductContact())
                        .section(section)
                        .build();

                Amplify.API.mutate(ModelMutation.delete(product),
                        response -> Log.i(TAG, "item deleted from API:" + product.getProductTitle()),
                        error -> Log.e(TAG, "Delete failed", error)
                );


                //delete from database
//                productDao.deleteProduct(products.get(position));

                //delete from tasks list
                products.remove(position);
                listItemDeleted();


                // delete from any local storage/cache if any

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);
        taskRecyclerView.setLayoutManager(linearLayoutManager);
        taskRecyclerView.setAdapter(adapter);
    }

    private void configureAmplify() {
        // configure Amplify plugins
        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());
            Log.i(TAG, "onCreate: Successfully initialized Amplify plugins");
        } catch (AmplifyException exception) {
            Log.e(TAG, "onCreate: Failed to initialize Amplify plugins => " + exception.toString());
        }
    }

    private void getTasksDataFromAPI() {
        Amplify.API.query(ModelQuery.list(Product.class),
                response -> {
                    for (Product product : response.getData()) {
                        products.add(product);
                        Log.i(TAG, "onCreate: the Tasks DynamoDB are => " + product.getProductTitle());
                    }
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e(TAG, "onCreate: Failed to get Tasks from DynamoDB => " + error.toString())
        );
    }

    @SuppressLint("NotifyDataSetChanged")
    private void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }


    public void saveTeamToApi(String sectionName) {
        Section section = Section.builder().sectionName(sectionName).build();

        Amplify.API.query(ModelQuery.list(Section.class, Section.SECTION_NAME.contains(sectionName)),
                response -> {
                    List<Section> sections = (List<Section>) response.getData().getItems();

                    if (sections.isEmpty()) {
                        Amplify.API.mutate(ModelMutation.create(section),
                                success -> Log.i(TAG, "Saved Team => " + section.getSectionName()),
                                error -> Log.e(TAG, "Could not save Team to API => ", error));
                    }
                },
                error -> Log.e(TAG, "Failed to get Team from DynamoDB => " + error.toString())
        );

    }

    private void getTeamTasksFromAPI(String sectionName) {
        Amplify.API.query(ModelQuery.list(Product.class),
                response -> {
                    for (Product product : response.getData()) {

                        if ((product.getSection().getSectionName().equals(sectionName))) {
                            products.add(product);
                            Log.i(TAG, "onCreate: the Tasks DynamoDB are => " + product.getProductTitle());
                            Log.i(TAG, "onCreate: the team DynamoDB are => " + product.getSection().getSectionName());
                        }
                    }
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e(TAG, "onCreate: Failed to get Tasks from DynamoDB => " + error.toString())
        );
    }

    private void downloadFile(String key) {
        Amplify.Storage.downloadFile(
                key,
                new File(getApplicationContext().getFilesDir() + key),
                result -> {
                    Log.i("MyAmplifyApp", "Successfully downloaded: " + result.getFile().getName());
                    ImageView image = (ImageView) findViewById(R.id.product_image);
                    image.setImageBitmap(BitmapFactory.decodeFile(result.getFile().getPath()));
                    image.setVisibility(View.VISIBLE);
                },
                error -> Log.e("MyAmplifyApp", "Download Failure", error)
        );
    }
}