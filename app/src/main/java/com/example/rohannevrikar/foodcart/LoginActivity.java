package com.example.rohannevrikar.foodcart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Button login;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_login);
        email = (EditText) findViewById(R.id.email);
        sharedPreferences = getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
        password = (EditText) findViewById(R.id.password);
        if(sharedPreferences.getBoolean("success",false)){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);


        }


        login = (Button) findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (("tannstafel@gmail.com").equals(email.getText().toString()) && ("user123").equals(password.getText().toString())) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("success", true);
                    editor.apply();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);



                } else {
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();

                }
            }
        });

    }
}

