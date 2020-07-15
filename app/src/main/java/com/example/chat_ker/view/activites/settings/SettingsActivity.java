package com.example.chat_ker.view.activites.settings;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.chat_ker.R;
import com.example.chat_ker.databinding.ActivitySettingsBinding;
import com.example.chat_ker.view.activites.profile.ProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_settings);

        setSupportActionBar(binding.toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
            if(firebaseUser!=null) {
                getInfo();
            }

      initClickAction();


    }

    private void initClickAction() {
        binding.lnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ProfileActivity.class));
            }
        });
    }

    private void getInfo() {            // collectionPath Users->Firebase
        firebaseFirestore.collection("User").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userName=documentSnapshot.get("userName").toString();    //firebase "userName" in database
                String imageProfile = documentSnapshot.getString("imageProfile");
                String userDesc=documentSnapshot.get("bio").toString();
                binding.tvUsername.setText(userName);
                binding.tvBio.setText(userDesc);

                    if (imageProfile != null && !imageProfile.equals("")) {
                        Glide.with(SettingsActivity.this).load(imageProfile).into(binding.imageProfile);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            binding.imageProfile.setImageDrawable(getDrawable(R.drawable.usernew));
                        }
                    }
                Glide.with(SettingsActivity.this).load(imageProfile).into(binding.imageProfile);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG","OnFailure"+e.getMessage());
            }
        });
    }
}