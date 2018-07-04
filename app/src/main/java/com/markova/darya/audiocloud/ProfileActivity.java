package com.markova.darya.audiocloud;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class ProfileActivity extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 101;
    EditText displayNameEditText;
    TextView emailVerifyTextView;
    ImageView imageView;
    ProgressBar uploadProgressBar;

    Uri uriProfileImage;
    String profileImageUrl;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        displayNameEditText = findViewById(R.id.editTextDisplayName);
        emailVerifyTextView = findViewById(R.id.emailVerifiedTextView);
        imageView = findViewById(R.id.imageView);
        uploadProgressBar = findViewById(R.id.progressbar);

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });

        findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            this.loadUserInformation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null) {
            uriProfileImage = data.getData();

            /*Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }*/

            uploadImageToFirebaseStorage();
        }
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select profile image"), CHOOSE_IMAGE);
    }

    private void uploadImageToFirebaseStorage() {
        StorageReference profileImageRef = FirebaseStorage.getInstance()
                .getReference("profile_images/" + System.currentTimeMillis() + ".jpg");

        if (uriProfileImage != null) {
            uploadProgressBar.setVisibility(View.VISIBLE);

            profileImageRef.putFile(uriProfileImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadProgressBar.setVisibility(View.GONE);
                        profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        uploadProgressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    private void saveUserInformation() {

        String displayName = displayNameEditText.getText().toString();

        if (displayName.isEmpty()) {
            displayNameEditText.setError("Name required");
            displayNameEditText.requestFocus();
            return;
        }
        uploadProgressBar.setVisibility(View.VISIBLE);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (profileImageUrl == null) {
            profileImageUrl = user.getPhotoUrl().toString();
        }

        if (user != null && profileImageUrl != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            uploadProgressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void loadUserInformation() {
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);
            }

            if (user.getDisplayName() != null) {
                displayNameEditText.setText(user.getDisplayName());
            }

            if (user.isEmailVerified()) {
                emailVerifyTextView.setText("Email Verified");
            } else {
                emailVerifyTextView.setText("Email Not Verified (Click to Verify)");
                emailVerifyTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfileActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
    }
}
