package com.example.chat_ker.menu;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chat_ker.R;
import com.example.chat_ker.adapter.ChatListAdapter;
import com.example.chat_ker.databinding.FragmentChatsBinding;
import com.example.chat_ker.model.Chatlist;
import com.example.chat_ker.view.activites.contact.ContactsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {


    private static final String TAG="ChatsFragment";

    public ChatsFragment() {
        // Required empty public constructor
    }


    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private FirebaseFirestore firestore;
    private Handler handler = new Handler();

    private List<Chatlist> list;

    private FragmentChatsBinding binding;

    private ArrayList<String> allUserId;

    private ChatListAdapter adapter;

    Calendar calendar;
    String CurrentDate;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_chats, container, false);
        list = new ArrayList<>();
        allUserId = new ArrayList<>();


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            calendar=Calendar.getInstance();
           CurrentDate=DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(calendar.getTime());
        }


        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext())) ;
        adapter = new ChatListAdapter(list,getContext());
        binding.recyclerView.setAdapter(adapter);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference=FirebaseDatabase.getInstance().getReference();
        firestore=FirebaseFirestore.getInstance();



        if (firebaseUser !=null){
            getChatList();


        }

        binding.fabAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ContactsActivity.class));
            }
        });

       return binding.getRoot();

     }

    private void getChatList() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        list.clear();
        allUserId.clear();

        reference.child("ChatList").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot  snapshot : dataSnapshot.getChildren() ){
                    String userId= Objects.requireNonNull(snapshot.child("chatid")).getValue().toString();
                    Log.d(TAG,"onDataChanged: userId"+userId);

                    binding.progressCircular.setVisibility(View.GONE);
                    allUserId.add(userId);
                }
                getUserInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getUserInfo() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (String userID : allUserId){
                    firestore.collection("User").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.d(TAG, "onSuccess: ddd"+documentSnapshot.getString("userName"));
                            try {
                                Chatlist chat = new Chatlist(
                                        documentSnapshot.getString("userId"),
                                        documentSnapshot.getString("userName"),
                                        documentSnapshot.getString("bio"),
                                        CurrentDate,
                                        documentSnapshot.getString("imageProfile")
                                );
                                list.add(chat);
                            }catch (Exception e){
                                Log.d(TAG, "onSuccess: "+e.getMessage());
                            }
                            if (adapter!=null){
                                adapter.notifyItemInserted(0);
                                adapter.notifyDataSetChanged();

                                Log.d(TAG, "onSuccess: adapter "+adapter.getItemCount());
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Error L"+e.getMessage());
                        }
                    });
                }
            }
        });

    }


}