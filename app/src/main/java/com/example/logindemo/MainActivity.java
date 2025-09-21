package com.example.logindemo;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;
import java.util.Locale;




public class MainActivity extends AppCompatActivity {

    // NEW: where RegistrationActivity saved users (key "user_<lowercase>", value password)
    private SharedPreferences usersPrefs;   // file name: "Users"

    @SuppressLint("ClickableViewAccessibility")
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

        // NEW: open the same prefs file used by RegistrationActivity
        usersPrefs = getSharedPreferences("Users", MODE_PRIVATE);

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
        Button cancelB = findViewById(R.id.cancel_button);
        TextView tvMessage = findViewById(R.id.tvMessage);
        TextView linkRegister = findViewById(R.id.linkRegister);

        //cancel button
        cancelB.setOnClickListener(v -> {
            userInput.getText().clear();
            passwordInput.getText().clear();
            rememberMeBox.setChecked(false);
            if (tvMessage != null) tvMessage.setText(""); // clear any status message
            userInput.requestFocus();
        });

        //Registration Link
        linkRegister.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class))
        );


        loginB.setOnClickListener(v -> {   //run when button is clicked : onclick listener for login button
            String username = userInput.getText().toString().trim();   //get input (password and username entered by user
            String password = passwordInput.getText().toString().trim();

            // 1) Look up a stored user created in RegistrationActivity
            String key = "user_" + username.toLowerCase(Locale.US);  // same key format
            String savedPass = usersPrefs.getString(key, null);
            boolean okFromPrefs = (savedPass != null && savedPass.equals(password));


            //boolean loginSuccess = true;

            //original passcode from steps 1-6
            boolean okHardcodedCredentials =
                    username.equalsIgnoreCase("admin") && password.equals("password");

            if (okFromPrefs || okHardcodedCredentials){    //check login validation

                Toast.makeText(this,"Login Successful!", Toast.LENGTH_SHORT).show();  //login successful


                if(rememberMeBox.isChecked()){          //check if remember me box is checked

                    //saves username to shared preference

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


        //Show Password Logic
        // Logic will follow something like  visibilityLogo > onClick > Make password visible
        passwordInput.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2; // Index for drawableEnd

            if (event.getAction() == MotionEvent.ACTION_UP
                    && passwordInput.getCompoundDrawables()[DRAWABLE_RIGHT] != null
                    && event.getRawX() >= (passwordInput.getRight()
                    - passwordInput.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()
                    - passwordInput.getPaddingEnd())) {

                boolean isHidden = passwordInput.getTransformationMethod()
                        instanceof PasswordTransformationMethod;

                if (isHidden) {
                    // Show text + switch to the "eye with slash"
                    passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordInput.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.outline_lock_24, 0, R.drawable.outline_visibility_off_24, 0
                    );
                } else {
                    // Hide text + switch back to the normal eye
                    passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordInput.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.outline_lock_24, 0, R.drawable.baseline_visibility_24, 0
                    );
                }

                // Move the cursor to the end of the text
                passwordInput.setSelection(passwordInput.getText().length());
                v.performClick();
                return true;
            }
            return false;
        });
    }
}
