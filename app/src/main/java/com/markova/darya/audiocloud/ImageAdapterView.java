package com.markova.darya.audiocloud;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.markova.darya.audiocloud.model.ImageFile;
import com.markova.darya.audiocloud.service.UploadFileService;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by daryamarkova on 09.07.2018.
 */

public class ImageAdapterView extends RecyclerView.Adapter<ImageAdapterView.ImageViewHolder> {
    private Context context;
    private List<ImageFile> imageList;

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public ImageView imageView;
        public TextView extTextView;
        public ImageButton optionButton;

        public ImageViewHolder(View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.textViewName);
            extTextView = itemView.findViewById(R.id.extTextView);
            imageView = itemView.findViewById(R.id.imagePreview);
            optionButton = itemView.findViewById(R.id.optionButton);
        }
    }


    public ImageAdapterView(Context context, List<ImageFile> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, final int position) {

        holder.optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view, position);
            }
        });

        ImageFile imageFile = imageList.get(position);
        String sourceTitle = imageFile.getGenerateTitle();
        String extension = imageFile.getContentType() != null ? imageFile.getContentType() : "unknown type";
        String showTitle = imageFile.getTitle();

        if (sourceTitle != null) {
            showTitle += sourceTitle.substring(sourceTitle.lastIndexOf('.'));
        }

        holder.titleTextView.setText(showTitle); //отображаем название с расширением
        holder.extTextView.setText(extension); //отобразить время

        Picasso.get().load(imageFile.getUrl())
                //.placeholder(R.drawable.upload_cloud_icon)
                //.fit()
                //.centerInside()
                .resize(120,120)
                .centerCrop()
                .into(holder.imageView);

    }

    private void showPopupMenu(View parent, final int position) {
        PopupMenu popupMenu = new PopupMenu(context, parent);
        //popupMenu.inflate(R.menu.file_action_menu);
        popupMenu.getMenuInflater().inflate(R.menu.file_action_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.menuDelete:
                        //удаление файла
                        final ImageFile selectedFile = imageList.get(position);
                        UploadFileService.deleteFile(selectedFile).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //подписаться и обработать события
                                UploadFileService.deleteMetaInfo(selectedFile.getKey());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context,  e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    case R.id.menuDownload:
                        //функционал загрузки файла
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return this.imageList.size();
    }
}
