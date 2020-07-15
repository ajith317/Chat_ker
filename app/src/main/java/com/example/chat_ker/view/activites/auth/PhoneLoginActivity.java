package com.example.chat_ker.view.activites.auth;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.chat_ker.R;
import com.example.chat_ker.databinding.ActivityPhoneLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private ActivityPhoneLoginBinding binding;
    private static  String TAG="PhoneLoginActiviy";

    private FirebaseAuth mAuth;
    private String mVerficationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private ProgressDialog progressDialog;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_phone_login);

        mAuth=FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null){
            startActivity(new Intent(this, SetUserInfoActivity.class));
        }



        progressDialog=new ProgressDialog(PhoneLoginActivity.this,R.style.AppTheme_WhiteAccent);
        
      binding.btnNext.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(binding.btnNext.getText().toString().equals("OTP AAVAD")) {

                  progressDialog.setMessage("Uvho Rhavo");
                  progressDialog.show();

                  String phone = "+"+binding.edCodeCountry.getText().toString()+binding.edPhone.getText().toString();
                  startPhoneNumberVerfication(phone);
              }
              else{
                  progressDialog.setMessage("Thura Number Verify Kereriyo...");
                  progressDialog.show();
                  verifyPhoneNumberWithCode(mVerficationId,binding.edCode.getText().toString());
              }
          }
      });


      mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

          @Override
          public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

              Log.d(TAG,"OnVerficationCompleted:Complete");
              signInWithPhoneAuthCredential(phoneAuthCredential);
              progressDialog.dismiss();

          }

          @Override
          public void onVerificationFailed(@NonNull FirebaseException e) {
              Log.d(TAG,"OnVerficationFailed:"+e.getMessage());

          }


          @Override
          public void onCodeSent(@NonNull String verificationId,
                                 @NonNull PhoneAuthProvider.ForceResendingToken token) {

              Log.d(TAG, "onCodeSent:" + verificationId);

                binding.btnNext.setText("Tuur");
                    progressDialog.dismiss();

                    mVerficationId = verificationId;
              mResendToken = token;


          }
      };
    }


    private void startPhoneNumberVerfication(String phoneNumber){
        progressDialog.setMessage("Ture Numberk Code Thadireyo:"+phoneNumber);
        progressDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        //Phone Number to Verify
                60,               //Time out Duration
                TimeUnit.SECONDS,    //Unit of timeOut
                this,           //Activity(for Callback binding)
                 mCallbacks);
      // mVerificationInProgress = true;

    }

    private void verifyPhoneNumberWithCode(String mVerficationId,String code){
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVerficationId,code);
        signInWithPhoneAuthCredential(credential);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            startActivity(new Intent(PhoneLoginActivity.this,SetUserInfoActivity.class));

                       /*     if (usernew!=null) {
                                String userID = usernew.getUid();
                                User users = new User(userID,
                                        "",
                                        usernew.getPhoneNumber(),
                                        "",
                                        "",
                                        "",
                                        "",
                                        "",
                                        "");

                                    firestore.collection("Users").document("UserInfo")
                                            .collection(userID).add(users).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            startActivity(new Intent(PhoneLoginActivity.this, SetUserInfoActivity.class));
                                        }
                                    });
                                }
                            else{
                                Toast.makeText(PhoneLoginActivity.this, "Serga Thekani", Toast.LENGTH_SHORT).show();
                            }*/


                        } else {
                            progressDialog.dismiss();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                             Log.d(TAG,"OnCompleter:Error Code");

                            }

                        }
                    }
                });
    }
}
