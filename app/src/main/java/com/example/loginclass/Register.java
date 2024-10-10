package com.example.loginclass;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText username1 = findViewById(R.id.username);
        EditText fullname1 = findViewById(R.id.fullname);
        EditText email1 = findViewById(R.id.email);
        EditText password1 = findViewById(R.id.password);
        EditText confirm_password1 = findViewById(R.id.confirm_password);
        Button register = findViewById(R.id.register_button);
        TextView login_redirect = findViewById(R.id.login_redirect);

        DBHelper database = new DBHelper(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = username1.getText().toString();
                final String fullname = fullname1.getText().toString();
                final String email = email1.getText().toString();
                final String password = password1.getText().toString();
                final String confirm_password = confirm_password1.getText().toString();

                if (username.equals("") || password.equals("") || confirm_password.equals("") || email.equals("") || fullname.equals("")) {
                    Toast.makeText(Register.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (password.equals(confirm_password)) {
                        boolean validate_user = database.viewSingleUser(username);
                        if (!validate_user) {
                            boolean insert_user = database.insertUser(username, email, fullname, password);
                            if (insert_user) {
                                Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Register.this, Dasboard.class);
                                startActivity(intent);
                                finish();
                                database.close();
                            } else {
                                Toast.makeText(Register.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Register.this, "User already exists", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Register.this, "Passwords not matching", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        login_redirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}