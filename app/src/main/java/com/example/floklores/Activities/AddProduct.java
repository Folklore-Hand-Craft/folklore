package com.example.floklores.Activities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.fRoom.Room;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Product;
import com.amplifyframework.datastore.generated.model.Section;
import com.example.floklores.Infrastructure.AppDatabase;
import com.example.floklores.Infrastructure.ProductDao;
import com.example.floklores.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddProduct extends AppCompatActivity {

    private static final String TAG = "AddTaskActivity";

    private ProductDao productDao;

    private String sectionId = "";

    private final List<Section> sections = new ArrayList<>();

    static String format = "dd-MM-yyyy";
    @SuppressLint("SimpleDateFormat")
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    private static String uploadedFileName = simpleDateFormat.format(new Date());
    private static String uploadedFileExtension = null;
    private static File uploadFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        setTitle("Add Task");
        getAllTeamsDataFromAPI();

        // states spinner
//        Spinner statesList = findViewById(R.id.spinner);
//        String[] states = new String[]{"New", "Assigned", "In progress", "Complete"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, states);
//        statesList.setAdapter(adapter);


        // teams spinner
        Spinner sectionsList = findViewById(R.id.spinnerSection);
        String[] sections = new String[]{"Team A", "Team B", "Team C"};
        ArrayAdapter<String> SectionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sections);
        sectionsList.setAdapter(SectionsAdapter);

//        upload file

        Button uploadFile = findViewById(R.id.upload_bt);
        uploadFile.setOnClickListener(v1 -> getFileFromDevice());


        // Room
//        AppDatabase database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "product_List")
//                .allowMainThreadQueries().build();
//        productDao = database.productDao();

        // add task button handler
        findViewById(R.id.buttonAddProduct).setOnClickListener(view -> {
            String productTitle = ((EditText) findViewById(R.id.editTextProductTitle)).getText().toString();
            String productBody = ((EditText) findViewById(R.id.editTextProductBody)).getText().toString();
            String productPrice = ((EditText) findViewById(R.id.editTextProductPrice)).getText().toString();
            String productContact = ((EditText) findViewById(R.id.editTextProductContact)).getText().toString();

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String location = sharedPreferences.getString("location", "");

//            Spinner spinner = (Spinner) findViewById(R.id.spinner);
//            String taskState = spinner.getSelectedItem().toString();

            Spinner sectionSpinner = (Spinner) findViewById(R.id.spinnerSection);
            String sectionName = sectionSpinner.getSelectedItem().toString();


            // save to room
//            Task newTask = new Task(taskTitle, taskBody, taskState);
//            taskDao.addTask(newTask);



            // get the Team from Database and it will update the teamId with the new id

            Log.i(TAG, "on button Listener the team id is >>>>> " + getTeamId(sectionName));

            // save task to dynamoDB
            addTaskToDynamoDB(productTitle,
                    productBody,
                    productPrice,
                    productContact,
                    new Section(getTeamId(sectionName), sectionName),
                    location
            );

            Intent goToMainActivity = new Intent(AddProduct.this, MainActivity.class);
            startActivity(goToMainActivity);

        });

        //Go Home!
        ImageView goHomeImage = findViewById(R.id.fromAddTaskActivityToHome);
        goHomeImage.setOnClickListener(v -> {
            // back button pressed
            Intent goHome = new Intent(AddProduct.this, MainActivity.class);
            startActivity(goHome);
        });
    }

    public void addTaskToDynamoDB(String productTitle, String productBody, String productPrice,String productContact, Section section, String location) {
        Product product = Product.builder()
                .productTitle(productTitle)
                .productBody(productBody)
                .productPrice(productPrice)
                .productContact(productContact)
                .section(section)
                .fileName(uploadedFileName +"."+ uploadedFileExtension.split("/")[1])
                .location(location)
                .build();

        Amplify.API.mutate(ModelMutation.create(product),
                success -> Log.i(TAG, "Saved item: " + product.getProductTitle()),
                error -> Log.e(TAG, "Could not save item to API", error));

        Amplify.Storage.uploadFile(
                uploadedFileName +"."+ uploadedFileExtension.split("/")[1],
                uploadFile,
                success -> {
                    Log.i(TAG, "uploadFileToS3: succeeded " + success.getKey());
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


    private void getAllTeamsDataFromAPI() {
        Amplify.API.query(ModelQuery.list(Section.class),
                response -> {
                    for (Section section : response.getData()) {
                        sections.add(section);
                        Log.i(TAG, "the team id DynamoDB are => " + section.getSectionName() + "  " + section.getId());
                    }
                },
                error -> Log.e(TAG, "onCreate: Failed to get team from DynamoDB => " + error.toString())
        );
    }

    public void getTeamFromApi(String sectionName) {
        Amplify.API.query(ModelQuery.list(Section.class, Section.SECTION_NAME.contains(sectionName)),
                response -> {
                    List<Section> sections = (List<Section>) response.getData().getItems();
                    Log.i(TAG, "get exist team id => " + sections.get(0).getId());
                    sectionId = sections.get(0).getId();
                },
                error -> {
                    Log.e(TAG, "onCreate: Failed to get Teams from DynamoDB => " + error.toString());
                }
        );

        Log.i(TAG, "the Team from the function => " + sectionName + " - " + sectionId);

    }

    public void saveTeamToApi(String sectionName) {
        Section section = Section.builder().sectionName(sectionName).build();

        Amplify.API.query(ModelQuery.list(Section.class, Section.SECTION_NAME.contains(sectionName)),
                response -> {
                    List<Section> sections = (List<Section>) response.getData().getItems();

                    if (sections.isEmpty()) {
                        Amplify.API.mutate(ModelMutation.create(section),
                                success -> Log.i(TAG, "Saved item: " + section.getSectionName()),
                                error -> Log.e(TAG, "Could not save item to API", error));
                    }
                },
                error -> Log.e(TAG, "onCreate: Failed to get Teams from DynamoDB => " + error.toString())
        );

    }

    public String getTeamId(String sectionName) {
        for (Section section : sections) {
            if (section.getSectionName().equals(sectionName)) {
                return section.getId();
            }
        }
        return "";
    }

}