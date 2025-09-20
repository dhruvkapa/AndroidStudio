package com.example.logindemo;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //check for a saved session from before first
        SharedPreferences sharedPrefer = getSharedPreferences("LoginStatus", MODE_PRIVATE);
        String savedUser = sharedPrefer.getString("username", null);

        if(savedUser != null){ //if username is saved, then user checked remember me session is valid and go to the welcome user
            Intent intent = new Intent(MainActivity.this, WelcomeScreen.class);
            intent.putExtra("username", savedUser);    //pass username
            startActivity(intent);
            finish();     //close login to prevent going back
            return;         //exit onCreate
        }

        //set views
        EditText userInput = findViewById(R.id.usernameInput);  //reference to userinput
        EditText passwordInput = findViewById(R.id.password);       //reference to password
        Button loginB = findViewById(R.id.login_button);     //reference to login button
        CheckBox rememberMeBox = findViewById(R.id.rememberMe);

        loginB.setOnClickListener(v -> {   //run when button is clicked : onclick listener for login button
            String username = userInput.getText().toString().trim();   //get input (password and username entered by user
            String password = passwordInput.getText().toString().trim();

            //boolean loginSuccess = true;

            if(username.equalsIgnoreCase("admin") && password.equalsIgnoreCase("password")){    //check login validation

                Toast.makeText(this,"Login Successful!", Toast.LENGTH_SHORT).show();  //login successful


                if(rememberMeBox.isChecked()){          //check if remember me box is checked

                    //saves username to sharedpreference
                    SharedPreferences.Editor editor = sharedPrefer.edit();
                    editor.putString("username", username);
                    editor.apply();         //saves data
                }

                //direct to welcome page
                Intent intent = new Intent(MainActivity.this, WelcomeScreen.class);
                intent.putExtra("username", username);          //pass to next activity
                startActivity(intent);

                finish();
            } else {
                Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();  //login failed
            }


        });





    }
}