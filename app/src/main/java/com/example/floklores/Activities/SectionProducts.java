package com.example.floklores.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Product;
import com.amplifyframework.datastore.generated.model.Section;
import com.example.floklores.Adapter.ProductAdapter;
import com.example.floklores.Models.ProductItem;
import com.example.floklores.R;

import java.util.ArrayList;
import java.util.List;

public class SectionProducts extends AppCompatActivity {

    private static final String TAG = "TeamTasks";
    private List<Product> productItems;
    private ProductAdapter adapter;

    private RecyclerView productRecyclerView;
    private LinearLayoutManager linearLayoutManager;


    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_products);
        setTitle("Team Tasks");


        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                notifyDataSetChanged();
                return false;
            }
        });

        //Go Home!
        ImageView goHomeImage = findViewById(R.id.fromSettingsActivityToHome);
        goHomeImage.setOnClickListener(v -> {
            // back button pressed
            Intent goHome = new Intent(SectionProducts.this, MainActivity.class);
            startActivity(goHome);
        });

    }

    private void listItemDeleted() {
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();

        //create sharedPreference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sectionName = sharedPreferences.getString("sectionName", "");

        productItems = new ArrayList<>();

        if (!sectionName.equals("")) {
            ((TextView) findViewById(R.id.textViewMyTeam)).setText(sectionName + " Tasks");
            getTeamTasksFromAPI(sectionName);

        }
//        getTeamTasksFromAPI("Team A");

//        for (Task task : tasks) {
//            Log.i(TAG, "team tasks: " + task.getTaskTitle());
//        }


        // RecycleView

        productRecyclerView = findViewById(R.id.list);
        adapter = new ProductAdapter(productItems, new ProductAdapter.OnTaskItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent goToDetailsIntent = new Intent(getApplicationContext(), Details.class);
                goToDetailsIntent.putExtra("productTitle", productItems.get(position).getProductTitle());
                goToDetailsIntent.putExtra("productBody", productItems.get(position).getProductBody());
                goToDetailsIntent.putExtra("productContact", productItems.get(position).getProductContact());
                goToDetailsIntent.putExtra("productPrice", productItems.get(position).getProductPrice());
                startActivity(goToDetailsIntent);
            }

            @Override
            public void onDeleteItem(int position) {
                // todo: chech the delete
                Section section = Section.builder().sectionName("Team A").build();

                // todo: delete from Amplify API
                Product product = Product.builder()
                        .productTitle(productItems.get(position).getProductTitle())
                        .productBody(productItems.get(position).getProductBody())
                        .productPrice(productItems.get(position).getProductPrice())
                        .productContact(productItems.get((position)).getProductContact())
                        .section(section)
                        .build();

                Amplify.API.mutate(ModelMutation.delete(product),
                        response -> Log.i(TAG, "item deleted from API:" + product.getProductTitle()),
                        error -> Log.e(TAG, "Delete failed", error)
                );

                //delete from tasks list
                productItems.remove(position);
                listItemDeleted();


                // delete from any local storage/cache if any

            }
        });

        linearLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);
        productRecyclerView.setLayoutManager(linearLayoutManager);
        productRecyclerView.setAdapter(adapter);


    }


    private void getTeamTasksFromAPI(String sectionName) {
        Amplify.API.query(ModelQuery.list(Product.class),
                response -> {
                    for (Product product : response.getData()) {

                        if ((product.getSection().getSectionName().equals(sectionName))) {
                            productItems.add(product);
                            Log.i(TAG, "onCreate: the Tasks DynamoDB are => " + product.getProductTitle());
                            Log.i(TAG, "onCreate: the team DynamoDB are => " + product.getSection().getSectionName());
                        }
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
}