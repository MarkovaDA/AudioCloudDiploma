package com.markova.darya.audiocloud.service;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;
import com.markova.darya.audiocloud.model.ImageFile;

/**
 * Created by daryamarkova on 07.07.2018.
 */
public class UploadFileService {
    //https://www.androidhive.info/2016/10/android-working-with-firebase-realtime-database/ - работе с БД

    private static StorageReference storageReference =
            FirebaseStorage.getInstance().getReference();


    private static DatabaseReference databaseReference =
            FirebaseDatabase.getInstance().getReference();

    public static UploadTask uploadFile(Uri fileUri, Context context) {

        StorageReference fileReference = storageReference
                .child("uploads")
                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .child(System.currentTimeMillis() + "." + getFileExtention(fileUri, context));

        return fileReference.putFile(fileUri);
    }
    public static void saveMetaInfo(ImageFile uploadedImage) {
        //сохраняем мета-информацию о загруженном файле
        //добавить еще расширение и дату
        String uploadId = databaseReference.push().getKey();
        databaseReference.child("uploads_info").child(uploadId).setValue(uploadedImage);
    }

    public static Task<Void> deleteFile(ImageFile file) {
       return FirebaseStorage.getInstance().getReferenceFromUrl(file.getUrl()).delete();
    }

    public static Task<Void> deleteMetaInfo(String key) {
        return databaseReference.child("uploads_info").child(key).removeValue();
    }

    private static String getFileExtention(Uri fileUri, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }

    //получаем список файлов
    public static DatabaseReference getImageFileStorage() {
       return databaseReference.child("uploads_info");
    }
}