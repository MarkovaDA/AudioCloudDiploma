package com.markova.darya.audiocloud;


import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.markova.darya.audiocloud.model.ImageFile;
import com.markova.darya.audiocloud.service.UploadFileService;
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

        Picasso.get().load(uploadImgUri).into(previewImg); //загрузка preview картинки (если большая то не грузится)

        uploadToCloudButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadImgUri == null) {
                    return;
                }

                uploadProgressBar.setVisibility(View.VISIBLE);

                UploadFileService.uploadFile(uploadImgUri, UploadActivity.this)
                //обрабатываем состояния загрузки файла (успешная загрузка файла)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(UploadActivity.this, "Uploaded Successful", Toast.LENGTH_LONG).show();

                    ImageFile uploadedImage = new ImageFile(fileNameEditText.getText().toString().trim(),
                            taskSnapshot.getDownloadUrl().toString());

                    uploadedImage.setContentType(taskSnapshot.getMetadata().getContentType());
                    uploadedImage.setGenerateTitle(taskSnapshot.getMetadata().getName());
                    uploadedImage.setCreationTime(taskSnapshot.getMetadata().getCreationTimeMillis());

                    UploadFileService.saveMetaInfo(uploadedImage);

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    /*double progressValue = (100 * taskSnapshot.getBytesTransferred())/ taskSnapshot.getTotalByteCount();
                    uploadProgressBar.setProgress((int)progressValue);*/
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    uploadProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
}
