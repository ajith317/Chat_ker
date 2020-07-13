package com.example.chat_ker.view.display;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActivityChooserView;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.chat_ker.R;
import com.example.chat_ker.common.Common;
import com.example.chat_ker.databinding.ActivityViewImageBinding;
import com.example.chat_ker.view.profile.ProfileActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

public class ViewImageActivity extends AppCompatActivity {
    private ActivityViewImageBinding binding;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding= DataBindingUtil.setContentView(this,R.layout.activity_view_image);
       binding.imageView.setImageBitmap(Common.IMAGE_BITMAP);



    }
}