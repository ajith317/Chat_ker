package com.example.chat_ker.view.activites.display;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.chat_ker.R;
import com.example.chat_ker.common.Common;
import com.example.chat_ker.databinding.ActivityViewImageBinding;
import com.jsibbold.zoomage.ZoomageView;

public class ViewImageActivity extends AppCompatActivity {

    private ActivityViewImageBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding= DataBindingUtil.setContentView(this,R.layout.activity_view_image);

        String url  = getIntent().getStringExtra("url");

        Glide.with(this)
                .load(url)
                .into(binding.imageView);

    }
}