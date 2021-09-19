package com.example.chat_ker.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.example.chat_ker.R;
import com.example.chat_ker.common.Common;
import com.example.chat_ker.model.chat.Chats;
import com.example.chat_ker.tools.AudioService;
import com.example.chat_ker.view.activites.display.ViewImageActivity;
import com.example.chat_ker.view.activites.profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    private List<Chats> list;
    private Context context;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private FirebaseUser firebaseUser;
    private Activity activity;

    private ImageButton tmpBtnPlay;
    private AudioService audioService;


    public ChatsAdapter(List<Chats> list, Context context, Activity activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.audioService=new AudioService(context);
    }

    public void setList(List<Chats> list){
        this.list=list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position));

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textMessage;
       // private  TextView textMessageSeen;
        private TextView textMessageTime;
        private LinearLayout layoutText,layoutImage,layoutVoice;
        private ImageView imageMessage;
        private ImageButton btnPlay;
        private ViewHolder tmpHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.tv_text_message);
            layoutImage =itemView.findViewById(R.id.layout_image);
            layoutText=itemView.findViewById(R.id.layout_text);
            imageMessage=itemView.findViewById(R.id.image_chat);
            layoutVoice=itemView.findViewById(R.id.layout_voice);
            btnPlay=itemView.findViewById(R.id.btn_play_chat);

           //textMessageSeen=itemView.findViewById(R.id.tv_text_message_seen);
         textMessageTime=itemView.findViewById(R.id.text_message_time);

        }
        void bind(Chats chats){

            //check chat type of Image or Text..
            switch (chats.getType()){
                case "TEXT":
                    layoutText.setVisibility(View.VISIBLE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVoice.setVisibility(View.GONE);

                    textMessage.setText(chats.getTextMessage());
                    textMessageTime.setText(chats.getDateTime());
                    break;

                case "IMAGE":
                    layoutText.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.VISIBLE);
                    layoutVoice.setVisibility(View.GONE);
                    Glide.with(context).load(chats.getUrl()).into(imageMessage);

                    Drawable db = imageMessage.getDrawable();

                    imageMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ViewImageActivity.class);
                            intent.putExtra("url", chats.getUrl());
                            context.startActivity(intent);
                        }
                    });

                    break;
                case "VOICE" :
                    layoutText.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVoice.setVisibility(View.VISIBLE);

                    layoutVoice.setOnClickListener(new View.OnClickListener() {
                        @Override

                        public void onClick(View v) {
                            if (tmpBtnPlay!=null){
                                tmpBtnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24));
                            }
                            btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_pause_circle_filled_24));
                            audioService.playAudioFromUrl(chats.getUrl(), new AudioService.OnPlayCallBack() {
                                @Override
                                public void onFinished() {
                                    btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24));
                                }
                            });
                            tmpBtnPlay=btnPlay;
                        }
                    });

                    break;
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (list.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else
        {
            return MSG_TYPE_LEFT;
        }

    }
}



