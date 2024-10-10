package com.example.loginclass;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private int userId;

    @SuppressLint({"LocalSuppress", "MissingInflatedId"})

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        EditText username1 = findViewById(R.id.username);
        EditText password1 = findViewById(R.id.password);
        TextView register1 = findViewById(R.id.register);
        Button login = findViewById(R.id.button);


        register1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });

        DBHelper helper = new DBHelper(this);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = username1.getText().toString();
                final String password = password1.getText().toString();

                if(username.isEmpty()||password.isEmpty()){
                    Toast.makeText(MainActivity.this, "Fields required", Toast.LENGTH_SHORT).show();
                } else {
                    boolean validate_user = helper.checkUserDetails(username, password);
                    if(validate_user){
                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        int userId = helper.getUserId(username);
                        Intent intent = new Intent(MainActivity.this, Dasboard.class);
                        intent.putExtra("USER_ID", userId);
                        startActivity(intent);
                        finish();
                        helper.close();

                    } else{
                        Toast.makeText(MainActivity.this, "Login Failed, Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }
}