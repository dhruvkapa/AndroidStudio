package com.example.logindemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
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

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            // Remove the remembered user so MainActivity doesn't auto-skip next time
            SharedPreferences sp = getSharedPreferences("LoginStatus", MODE_PRIVATE);
            sp.edit().remove("username").apply();     // or .clear() if you want to wipe all

            // Go back to Login and clear this screen from the back stack
            Intent i = new Intent(WelcomeScreen.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish(); // finish Welcome so back button won't return here
        });

    }
}