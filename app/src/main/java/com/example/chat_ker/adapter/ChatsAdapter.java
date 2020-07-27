package com.example.chat_ker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat_ker.R;
import com.example.chat_ker.model.chat.Chats;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    private List<Chats> list;
    private Context context;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private FirebaseUser firebaseUser;

    public ChatsAdapter(List<Chats> list, Context context) {
        this.list = list;
        this.context = context;
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
       Chats chats=list.get(position);

        /*if (position==list.size()-1){
            if (chats.getIsseen()){
                holder.textMessageSeen.setText("Seen");

            }else {
                holder.textMessageSeen.setText("Delieverd");
            }
        }
        else {
            holder.textMessageSeen.setVisibility(View.GONE);
        }*/
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textMessage;
       // private  TextView textMessageSeen;
        private TextView textMessageTime;
        private LinearLayout layoutText,layoutImage;
        private ImageView imageMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.tv_text_message);
            layoutImage =itemView.findViewById(R.id.layout_image);
            layoutText=itemView.findViewById(R.id.layout_text);
            imageMessage=itemView.findViewById(R.id.image_chat);

           //textMessageSeen=itemView.findViewById(R.id.tv_text_message_seen);
         textMessageTime=itemView.findViewById(R.id.text_message_time);

        }
        void bind(Chats chats){

            //check chat type of Image or Text..
            switch (chats.getType()){
                case "TEXT":
                    layoutText.setVisibility(View.VISIBLE);
                    layoutImage.setVisibility(View.GONE);

                    textMessage.setText(chats.getTextMessage());
                    textMessageTime.setText(chats.getDateTime());
                    break;

                case "IMAGE":
                    layoutText.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.VISIBLE);

                    Glide.with(context).load(chats.getUrl()).into(imageMessage);

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



