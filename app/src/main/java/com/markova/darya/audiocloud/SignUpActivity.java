package com.markova.darya.audiocloud;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    EditText editTextEmail, editTextPassword;
    ProgressBar signUpProgressBar;
    final int MIN_PASSWORD_LENGTH = 6;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        findViewById(R.id.textViewLogin).setOnClickListener(this);
        findViewById(R.id.buttonSignUp).setOnClickListener(this);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword =  findViewById(R.id.editTextPassword);
        signUpProgressBar = findViewById(R.id.progressbarSignUp);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.textViewLogin) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (view.getId() == R.id.buttonSignUp) {
            registerUser();
        }
    }

    private void registerUser() {
        //добавить поле confirmPassword
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            showErrorMessage(editTextEmail, "Email is required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showErrorMessage(editTextEmail, "Please, enter a valid email");
            return;
        }

        if (password.isEmpty()) {
            showErrorMessage(editTextPassword, "Password is required");
            return;
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            showErrorMessage(editTextPassword, "Minimum length of password should be 6");

            return;
        }

        signUpProgressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                signUpProgressBar.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    startActivity(new Intent(SignUpActivity.this, ProfileActivity.class));
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), R.string.email_busy_msg, Toast.LENGTH_SHORT);
                    }
                    Toast.makeText(getApplicationContext(), R.string.unknown_error_msg, Toast.LENGTH_SHORT).show();
                }
                }
            });
    }

    private void showErrorMessage(EditText editText, String message) {
        editText.setError(message);
        editText.requestFocus();
    }
}
