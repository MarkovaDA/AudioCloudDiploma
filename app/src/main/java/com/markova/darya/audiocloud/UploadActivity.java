package com.markova.darya.audiocloud;


import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;


public class UploadActivity extends AppCompatActivity {
    private Uri uploadImgUri;

    Button uploadToCloudButton;
    ProgressBar uploadProgressBar;
    ImageView previewImg;
    EditText fileNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadToCloudButton = findViewById(R.id.uploadToCloudBtn);
        uploadProgressBar = findViewById(R.id.uploadProgressBar);
        previewImg = findViewById(R.id.imagePreview);

        fileNameEditText = findViewById(R.id.fileNameEditText);
        uploadImgUri = Uri.parse(getIntent().getStringExtra("imagePath"));

        Picasso.get().load(uploadImgUri).into(previewImg);

        uploadToCloudButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //будем отправлять изображение в облако
            }
        });
    }

    private void uploadImage() {

    }
}
