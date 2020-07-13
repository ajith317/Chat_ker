package com.example.chat_ker.view.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.chat_ker.R;
import com.example.chat_ker.databinding.ActivitySetUserInfoBinding;
import com.example.chat_ker.model.user.User;
import com.example.chat_ker.view.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SetUserInfoActivity extends AppCompatActivity {

    private ActivitySetUserInfoBinding binding;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding= DataBindingUtil.setContentView(this,R.layout.activity_set_user_info);

       progressDialog=new ProgressDialog(this,R.style.AppTheme_WhiteAccent);
       initButtonClick();
    }

    private void initButtonClick() {
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(binding.edName.getText().toString())) {
                    Toast.makeText(SetUserInfoActivity.this, "Naav Nhi", Toast.LENGTH_SHORT).show();
                }else {
                    doUpdate();
                }
            }
        });
        binding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SetUserInfoActivity.this, "Avasaram no innum panala  ", Toast.LENGTH_SHORT).show();

            }
        });
    }
    private  void doUpdate(){
        progressDialog.setMessage("Vuo Rhavo...");
        progressDialog.show();

        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser !=null){
            String userID = firebaseUser.getUid();
            User users = new User(userID,
                    binding.edName.getText().toString(),
                    firebaseUser.getPhoneNumber(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "");

          firebaseFirestore.collection("User").document(firebaseUser.getUid())
                  .set(users)
                  //.update("userName",binding.edName.getText().toString())
                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void aVoid) {
                          progressDialog.dismiss();
                          Toast.makeText(getApplicationContext(), "Machithireyo", Toast.LENGTH_SHORT).show();
                          startActivity(new Intent(getApplicationContext(), MainActivity.class));

                      }
                  }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                  progressDialog.dismiss();
                  Log.d("Update", "onFailure: "+e.getMessage());
                  Toast.makeText(getApplicationContext(), "Upadate Failed", Toast.LENGTH_SHORT).show();
              }
          });

        }else {
            Toast.makeText(this, "Mulo Login Keruvo", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }
}