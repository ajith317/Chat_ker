package com.example.chat_ker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        if (position==list.size()-1){
            if (chats.isIsseen()){
                holder.textMessageSeen.setText("Seen");
            }else {
                holder.textMessageSeen.setText("Deleiverd");
            }

        }
        else {
            holder.textMessageSeen.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textMessage;
        private  TextView textMessageSeen;
        private TextView textMessageTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.tv_text_message);
            textMessageSeen=itemView.findViewById(R.id.tv_text_message_seen);
          //textMessageTime=itemView.findViewById(R.id.tv_text_message_time);

        }
        void bind(Chats chats){
            textMessage.setText(chats.getTextMessage());


           // textMessageTime.setText(chats.getDateTime());
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
