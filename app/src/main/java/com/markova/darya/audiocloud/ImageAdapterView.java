package com.markova.darya.audiocloud;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.markova.darya.audiocloud.model.ImageFile;
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

        public ImageViewHolder(View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.textViewName);
            imageView = itemView.findViewById(R.id.imagePreview);
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
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        ImageFile imageFile = imageList.get(position);
        holder.titleTextView.setText(imageFile.getTitle());
        Picasso.get().load(imageFile.getUrl())
                //.placeholder(R.drawable.upload_cloud_icon)
                .fit()
                .centerInside()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return this.imageList.size();
    }
}
