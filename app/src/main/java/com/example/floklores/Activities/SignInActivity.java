package com.example.floklores.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;
import com.example.floklores.R;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferenceEditor = preferences.edit();



        Button signIn = findViewById(R.id.btnlogin);
        EditText username = findViewById(R.id.etemail);
        EditText password = findViewById(R.id.mypass);
        TextView createNewAccount = findViewById(R.id.createnewac);

        signIn.setOnClickListener(view -> {
            signIn(username.getText().toString(), password.getText().toString());
            preferenceEditor.putString("UserNameLog", username.getText().toString());
            preferenceEditor.apply();
        });

        createNewAccount.setOnClickListener(view -> {
            Intent goToSignUp = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(goToSignUp);
        });


    }

    void signIn(String username, String password) {
        Amplify.Auth.signIn(
                username,
                password,
                success -> {
                    Log.i(TAG, "signIn: worked " + success.toString());
                    Intent goToMain = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(goToMain);
                },
                error -> Log.e(TAG, "signIn: failed" + error.toString()));


    }
}