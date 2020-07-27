package com.example.chat_ker.view.activites.chats;

import android.annotation.SuppressLint;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat_ker.R;
import com.example.chat_ker.adapter.ChatsAdapter;
import com.example.chat_ker.databinding.ActivityChatsBinding;
import com.example.chat_ker.managers.ChatService;
import com.example.chat_ker.managers.interfaces.OnReadChatCallBack;
import com.example.chat_ker.model.chat.Chats;
import com.example.chat_ker.service.FirebaseService;
import com.example.chat_ker.view.activites.dialog.DialogReviewSendImage;
import com.example.chat_ker.view.activites.profile.ProfileActivity;
import com.example.chat_ker.view.activites.profile.UserProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import static com.example.chat_ker.view.MainActivity.updateLastSeen;

public class ChatsActivity extends AppCompatActivity {
    private static final String TAG = "ChatsActivity";
    private  FirebaseFirestore db;
    private ActivityChatsBinding binding;
    private  FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private String receiverID;
    private ChatsAdapter adapter;
    private List<Chats> list;
    private String userProfile, userName, status;
    private boolean isActionShown=false;
    private LinearLayoutManager linearLayoutManager;
    private TextView tvStatus;
  ValueEventListener seenListener;
  private ChatService chatsService;
    private Uri imageUri;
    private int IMAGE_GALLERY_REQUEST = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chats);

        intialzie();
        initBtnClick();
        readChats();


    }

    private void intialzie(){

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();


        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        status = intent.getStringExtra("status");   //status
        receiverID = intent.getStringExtra("userId");
        userProfile = intent.getStringExtra("userProfile");

        chatsService =new ChatService(this,receiverID);

        if (receiverID != null) {
            binding.tvUsername.setText(userName);
            if (userProfile != null && !userProfile.equals("")) {
                Glide.with(ChatsActivity.this).load(userProfile).into(binding.imageProfile);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.imageProfile.setImageDrawable(getDrawable(R.drawable.usernew));
                }
            }
        }

        db = FirebaseFirestore.getInstance();
        if(firebaseUser != null) {

            db.collection("User").document(receiverID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Object o = document.getData().get("lastSeen");
                            if (o != null) {
                                binding.status.setText("Kadasi Paarvo :  "+   o.toString());
                            } else {
                                tvStatus.setText("Offline");
                            }
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });






            binding.viewProfile.setOnClickListener(    new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(ChatsActivity.this, ProfileActivity.class);
                    intent1.putExtra("isChatProfile", true);
                    intent1.putExtra("receiverID", receiverID);
                    intent1.putExtra("userName", userName);
                    intent1.putExtra("userProfile", userProfile);
                    startActivity(intent1);
                }
            });




        }


        binding.edMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("NewApi")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(binding.edMessage.getText().toString())){
                    binding.btnSend.setImageDrawable(getDrawable(R.drawable.ic_baseline_keyboard_voice_24));

                } else {
                    binding.btnSend.setImageDrawable(getDrawable(R.drawable.ic_baseline_send_24));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        list = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new ChatsAdapter(list,this);
        binding.recyclerView.setAdapter(adapter);
    }

    private void readChats() {
        chatsService.readChatData(new OnReadChatCallBack() {
            @Override
            public void onReadSuccess(List<Chats> list) {
             adapter.setList(list);
                if (adapter!=null){
                    adapter.notifyDataSetChanged();
                    linearLayoutManager.scrollToPosition(list.size() - 1);
                }else {
                    adapter = new ChatsAdapter(list,ChatsActivity.this);
                    binding.recyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onReadFailed() {
                Log.d(TAG,"ONReadFailed:");
            }
        });
    }

    private void initBtnClick(){

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(binding.edMessage.getText().toString())){
                 chatsService.sendTextMessage(binding.edMessage.getText().toString());
                    binding.edMessage.setText("");
                }
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

       binding.btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isActionShown) {
                    binding.layoutActions.setVisibility(View.GONE);
                    isActionShown=false;
                }else
                {
                    binding.layoutActions.setVisibility(View.VISIBLE);
                    isActionShown=true;
                }
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatsActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openGallery();
            }
        });

        // UserProfileActiviy
     /*  binding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ChatsActivity.this, UserProfileActivity.class)
                        .putExtra("receiverID",receiverID)
                        .putExtra("userProfile",userProfile)
                        .putExtra("userName",userName));
            }
        });*/
            // seenMessage(receiverID);
    }
    private void openGallery(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
      startActivityForResult(Intent.createChooser(intent, "Image Kaithi Dhovluvo"), IMAGE_GALLERY_REQUEST);
    /*     CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);*/
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_GALLERY_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null){
            imageUri = data.getData();
         //   uploadToFirebase();
             try {
                 Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    reviewImage(bitmap);

             }catch (Exception e){
                 e.printStackTrace();
             }

        }
        /*if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK
            ) {
                imageUri = result.getUri();
               uploadToFirebase();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }*/

    }

    private void reviewImage(Bitmap  bitmap){
        new DialogReviewSendImage(ChatsActivity.this,bitmap).show(new DialogReviewSendImage.OnCallBack() {
            @Override
            public void onButtonSendClick() {
                // to Upload Image to firebase storage to get url image...
                if (imageUri!=null){
                  final   ProgressDialog progressDialog=new ProgressDialog(ChatsActivity.this);
                    progressDialog.setMessage("Image Thaderiyoo...");
                    progressDialog.show();

                    //hide action buttonss
                    binding.layoutActions.setVisibility(View.GONE);
                    isActionShown = false;

                    new FirebaseService(ChatsActivity.this).uploadImageToFireBaseStorage(imageUri, new FirebaseService.OnCallBack() {
                        @Override
                        public void onUploadSuccess(String imageUrl) {
                            // to send chat image//
                            chatsService.sendImage(imageUrl);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onUploadFailed(Exception e) {
                            e.printStackTrace();
                        }
                    });

                 }
            }
        });
    }







  /* private void sendTextMessage(String text){
        Calendar currentDateTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        String currentTime = df.format(currentDateTime.getTime());

        Chats chats=new Chats(
                currentTime,
                text,
                "TEXT",
                firebaseUser.getUid(),
                receiverID,
                false
        );

        reference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Send", "onSuccess: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Send", "onFailure: "+e.getMessage());
            }
        });

        //Add to ChatList
        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
        chatRef1.child("chatid").setValue(receiverID);

        //
        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
        chatRef2.child("chatid").setValue(firebaseUser.getUid());

        adapter.notifyDataSetChanged();
        linearLayoutManager.scrollToPosition(list.size() - 1);

    }*/

    /*private void readChats(){
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child("Chats").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    list.clear();
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){


                          Chats chats=null;
                          //Chats chats = snapshot.getValue(Chats.class);
                        try{
                            Object obj= snapshot.getValue();
                            Gson gson=new Gson();
                            JsonElement json=gson.toJsonTree(obj);
                            chats=gson.fromJson(json,Chats.class);
                        }catch(Exception e){
                            Log.d("EXCEPTION",e.getMessage().toString());
                        }
                        if (chats != null && chats.getSender().equals(firebaseUser.getUid()) && chats.getReceiver().equals(receiverID)) {
                            list.add(chats);
                        }
                        if(chats != null && chats.getSender().equals(receiverID) && chats.getReceiver().equals(firebaseUser.getUid())){

                            list.add(chats);

                        }

                    }
                    if (adapter!=null){
                        adapter.notifyDataSetChanged();
                        linearLayoutManager.scrollToPosition(list.size() - 1);


                    }else {
                        adapter = new ChatsAdapter(list,ChatsActivity.this);
                        binding.recyclerView.setAdapter(adapter);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }

    }*/

    @Override
    protected void onStart() {
        super.onStart();
        Map u = new HashMap();
        u.put("lastSeen", "online");
        db.collection("User").document(firebaseUser.getUid()).update(u);
    }
    @Override
    protected void onPause() {
        super.onPause();
        updateLastSeen();

    }


}