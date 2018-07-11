package com.markova.darya.audiocloud;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.markova.darya.audiocloud.model.ImageFile;
import com.markova.darya.audiocloud.service.UploadFileService;

import java.util.ArrayList;
import java.util.List;

public class WorkActivity extends AppCompatActivity {

    FloatingActionButton uploadActionBtn;
    Toolbar actionToolbar;
    RecyclerView imageRecycleView;
    ValueEventListener dataNotifier;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private ImageAdapterView imageAdapterView;
    private List<ImageFile> imageFileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        uploadActionBtn = findViewById(R.id.uploadActBtn);
        actionToolbar = findViewById(R.id.actionToolbar);
        imageRecycleView = findViewById(R.id.recycleView);
        imageRecycleView.setHasFixedSize(true);
        imageRecycleView.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(actionToolbar);

        uploadActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();

            }
        });


        imageFileList = new ArrayList();
        imageAdapterView = new ImageAdapterView(WorkActivity.this, imageFileList);

        //подгрузка картинок из БД (использовать grid view для просмотра изображений)
        dataNotifier = UploadFileService.getImageFileStorage().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageFileList.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    ImageFile image = snapshot.getValue(ImageFile.class);
                    image.setKey(snapshot.getKey());
                    imageFileList.add(image);
                }

                imageAdapterView.notifyDataSetChanged();
                imageRecycleView.setAdapter(imageAdapterView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(WorkActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.work_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuLogout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;

            case R.id.menuProfile:
                //startActivity(new Intent(this, ProfileActivity.class));
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            Intent previewIntent = new Intent(WorkActivity.this, UploadActivity.class);
            previewIntent.putExtra("imagePath", selectedImageUri.toString());
            startActivity(previewIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        UploadFileService.getImageFileStorage()
                .removeEventListener(dataNotifier);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

}
