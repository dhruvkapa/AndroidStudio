package com.example.logindemo;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;
import androidx.core.content.ContextCompat;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // ---- wire up views ----
        newUsername= findViewById(R.id.newUsername);
        newPassword     = findViewById(R.id.newPassword);
        passwordConfirm = findViewById(R.id.passwordConfirm);
        tvRegMsg        = findViewById(R.id.tvRegMsg);
        Button btnCreate = findViewById(R.id.btnCreate);
        Button btnCancel = findViewById(R.id.btnCancel);

        // Open (or create) the "Users" preferences file
        usersPrefs = getSharedPreferences("Users", MODE_PRIVATE);

        //same eye-icon show/hide behavior used on the login screen
        attachShowHideToggle(newPassword);
        attachShowHideToggle(passwordConfirm);

        // Create account flow
        btnCreate.setOnClickListener(v -> tryCreateAccount());

        // Cancel just clears the fields/message (stays on this screen)
        btnCancel.setOnClickListener(v -> {
            newUsername.setText("");
            newPassword.setText("");
            passwordConfirm.setText("");
            tvRegMsg.setText("");
            newUsername.requestFocus();
        });
    }

    // 1) References to the views in activity_registration.xml
    private EditText newUsername, newPassword, passwordConfirm;
    private TextView tvRegMsg;

    // 2) Tiny on-device key/value store just for users
    //    We'll save each user as: key = "user_<lowercase username>", value = password
    private SharedPreferences usersPrefs;

    /** Validate inputs and save the new user. */
    private void tryCreateAccount() {
        String username = newUsername.getText().toString().trim();
        String pass1    = newPassword.getText().toString();
        String pass2    = passwordConfirm.getText().toString();

        // A) All fields required
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(pass1) || TextUtils.isEmpty(pass2)) {
            showMessage("Please fill in all fields.", true);
            return;
        }

        // B) Format checks (check with team)
        if (!isValidUsername(username)) {
            showMessage("Username must be letters/numbers, min 3 chars.", true);
            return;
        }
        if (!isValidPassword(pass1)) {
            showMessage("Password must be at least 4 characters.", true);
            return;
        }
        if (!pass1.equals(pass2)) {
            showMessage("Passwords do not match.", true);
            return;
        }

        // C) Ensure username is not already taken
        String key = userKey(username);
        if (usersPrefs.contains(key)) {
            showMessage("That username is already taken.", true);
            return;
        }

        // D) Save the user: key = user_<username>, value = password
        usersPrefs.edit().putString(key, pass1).apply();

        // E) Done! Tell the user and return to Login
        showMessage("Account created. You can log in now.", false);
        Toast.makeText(this, "Registered ✓", Toast.LENGTH_SHORT).show();

        finish(); // close RegistrationActivity (back to login screen)
    }

    // ---------------- helper methods ----------------

    /** Build the SharedPreferences key for a username. */
    private String userKey(String username) {
        return "user_" + username.toLowerCase(Locale.US);
    }

    private boolean isValidUsername(String s) {
        // letters, digits, underscore, dot, dash; at least 3 chars
        return s.length() >= 3 && s.matches("[A-Za-z0-9_.-]+");
    }

    private boolean isValidPassword(String s) {
        return s.length() >= 4;
    }

    /** Show a message in tvRegMsg (red for error, secondary for normal). */
    private void showMessage(String msg, boolean isError) {
        tvRegMsg.setText(msg);
        int color = ContextCompat.getColor(
                this,
                isError ? android.R.color.holo_red_dark : R.color.text_secondary
        );
        tvRegMsg.setTextColor(color);
    }

    //Adds the “eye” toggle behavior the same approach used in MainActivity for the login password field.
    @SuppressLint("ClickableViewAccessibility")
    private void attachShowHideToggle(EditText field) {
        final int DRAWABLE_RIGHT = 2;

        field.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP
                    && field.getCompoundDrawables()[DRAWABLE_RIGHT] != null
                    && event.getRawX() >= (field.getRight()
                    - field.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()
                    - field.getPaddingEnd())) {

                boolean wasHidden = field.getTransformationMethod() instanceof PasswordTransformationMethod;

                if (wasHidden) {
                    // Show password + switch to the "eye with slash" icon
                    field.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    field.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.outline_lock_24, 0, R.drawable.outline_visibility_off_24, 0
                    );
                } else {
                    // Hide password + switch back to the normal "eye" icon
                    field.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    field.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.outline_lock_24, 0, R.drawable.baseline_visibility_24, 0
                    );
                }

                // Keep cursor at the end
                field.setSelection(field.getText().length());
                return true;
            }
            return false;
        });
    }
}
