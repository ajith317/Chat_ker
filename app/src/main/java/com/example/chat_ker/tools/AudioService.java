package com.example.chat_ker.tools;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

public class AudioService {
    private Context context;
    private MediaPlayer tmpmediaPlayer;


  public AudioService(Context context){

        this.context=context;

    }

    public void playAudioFromUrl(String url,OnPlayCallBack onPlayCallBack){

        if(tmpmediaPlayer!=null){
           tmpmediaPlayer.stop();

        }
        MediaPlayer mediaPlayer=new MediaPlayer();
         try{

             mediaPlayer.setDataSource(url);
             mediaPlayer.prepare();
             mediaPlayer.start();

             tmpmediaPlayer=mediaPlayer;
         } catch (IOException e) {
             e.printStackTrace();
         }
         mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
             @Override
             public void onCompletion(MediaPlayer mp) {

                 onPlayCallBack.onFinished();
             }
         });
    }
    public interface  OnPlayCallBack{
        void onFinished();

    }}
