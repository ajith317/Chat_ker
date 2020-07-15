package com.example.chat_ker.notification;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.chat_ker.R;
import com.example.chat_ker.model.chat.Chats;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class NotificationService extends com.google.firebase.messaging.FirebaseMessagingService {
    public static int no=0;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("RemoteMessage",remoteMessage.getData().toString());
        JSONObject json= new JSONObject(remoteMessage.getData());
        try {
            int rn=new Random().nextInt(((1000-1)+1)+1);
            Intent intent = new Intent(getApplicationContext(), Chats.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
            notificationBuilder.setContentTitle(json.getString("title"));
            no++;
            if(no==1){
                notificationBuilder.setContentText(no+" new Message");

            }else{
                notificationBuilder.setContentText(no+" new Messages");
            }
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationBuilder.setSound(alarmSound);
            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setSmallIcon(R.drawable.notification);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setChannelId("1111");
            Log.d("Notification", "true");
            notificationBuilder.setGroup("xyz");
            notificationBuilder.setGroupSummary(false);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel("1111", "11111", NotificationManager.IMPORTANCE_HIGH));
            notificationManager.notify(rn, notificationBuilder.build());

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("New Token", s);
        SharedPreferences preferences = getSharedPreferences("chatKer", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Token", s);
        editor.commit();
        FirebaseUser fuser=FirebaseAuth.getInstance().getCurrentUser();
        if (fuser!= null ) {
            Map<String,String> doc=new HashMap<>();
            doc.put("token",s);
            doc.put("id",fuser.getUid());
            FirebaseFirestore.getInstance().collection("notification").document(fuser.getUid()).set(doc, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d("Token:","AddedSuccessfully from msgService");
                    }
                    else{
                        Log.d("Token:",task.getException().getMessage());
                    }
                }
            });

        }
    }
}
