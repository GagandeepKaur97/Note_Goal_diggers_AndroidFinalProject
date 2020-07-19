package com.example.note_goal_diggers_android;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class IconAdapter extends BaseAdapter {

    Context context;

    public int position;
    String photopath;
    Bitmap photobitmap;

    List<CategoryModel> categoryModelList;

    public IconAdapter(Context context, List<CategoryModel> categoryModelList) {
        this.context = context;
        this.categoryModelList = categoryModelList;
    }
    @Override
    public int getCount() {
        return categoryModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.gridview_layout,null);



        TextView title = convertView.findViewById(R.id.subjectTitle);

        TextView date = convertView.findViewById(R.id.ndate);
        ImageView image = convertView.findViewById(R.id.imgEmp);


        title.setText(categoryModelList.get(position).getTitle());

        date.setText(categoryModelList.get(position).getDate());

        photopath = categoryModelList.get(position).getImage();

        if(photopath != null){
            try {
                photobitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(photopath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            image.setImageBitmap(photobitmap);
        }


        return convertView;
    }
}
