package com.example.logindemo;

import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Toast;


public class WelcomeScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView welcomeMessageTextView = findViewById(R.id.welcome_message_view);      //find text view that display welcome message

        Intent intent = getIntent();    //intent to start activity

        String username = intent.getStringExtra("username");        //get username passed from login page


        if(username != null && !username.isEmpty()){
            String welcomeMessage = String.format(getString(R.string.welcome_message), username);       //insert username in the welcome message to personalize
            welcomeMessageTextView.setText(welcomeMessage);         //set message to textview
        }


    }
}