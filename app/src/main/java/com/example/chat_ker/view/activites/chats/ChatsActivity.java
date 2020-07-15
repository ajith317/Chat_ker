package com.example.chat_ker.view.activites.chats;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import com.example.chat_ker.model.chat.Chats;
import com.example.chat_ker.view.activites.profile.ProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {
    private static final String TAG = "ChatsActivity";
    private ActivityChatsBinding binding;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private String receiverID;
    private ChatsAdapter adapter;
    private List<Chats> list;
    private String userProfile, userName, status;
    private boolean isActionShown=false;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chats);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        status = intent.getStringExtra("status");   //status
        receiverID = intent.getStringExtra("userId");
        userProfile = intent.getStringExtra("userProfile");


        if (receiverID != null) {
            binding.tvUsername.setText(userName);
           // binding.status.setText(status);
            if (userProfile != null && !userProfile.equals("")) {
                Glide.with(ChatsActivity.this).load(userProfile).into(binding.imageProfile);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.imageProfile.setImageDrawable(getDrawable(R.drawable.usernew));
                }
            }

      /*  if (receiverID!=null){
            binding.toolbar.setTitle(userName);
            if (userProfile != null) {
                if (userProfile.equals("")){
                    binding.imageProfile.setImageResource(R.drawable.usernew);  // set  default image when profile user is null
                } else {
                    Glide.with(this).load(userProfile).into( binding.imageProfile);
                }
            }
        }

*/
         binding.viewProfile.setOnClickListener(new View.OnClickListener() {
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

        initBtnClick();

        list = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        linearLayoutManager.setStackFromEnd(true);

        binding.recyclerView.setLayoutManager(linearLayoutManager);

        readChats();

    }

    private void initBtnClick(){

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(binding.edMessage.getText().toString())){
                    sendTextMessage(binding.edMessage.getText().toString());
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

     /*  binding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ChatsActivity.this, UserProfileActivity.class)
                        .putExtra("receiverID",receiverID)
                        .putExtra("userProfile",userProfile)
                        .putExtra("userName",userName));
            }
        });*/

    }

    private void sendTextMessage(String text){

        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String today = formatter.format(date);

        Calendar currentDateTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        String currentTime = df.format(currentDateTime.getTime());

        
        Chats chats = new Chats(
                today+", "+currentTime,
                text,
                "TEXT",
                firebaseUser.getUid(),
                receiverID
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

    }

    private void readChats(){
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child("Chats").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    list.clear();
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                        Chats chats = snapshot.getValue(Chats.class);
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

    }
}