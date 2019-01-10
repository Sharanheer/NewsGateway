package com.example.sharan.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

import java.util.ArrayList;

public class NewsService extends Service {

    private static String ACTION_MSG_TO_SERVICE="ACTION_MSG_TO_SERVICE";
    private static String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    private boolean isRunning = true;
    private ArrayList<Article> articleList = new ArrayList<Article>();
    private ServiceReceiver serviceReceiver;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        serviceReceiver = new ServiceReceiver();
        IntentFilter filter = new IntentFilter(ACTION_MSG_TO_SERVICE);
        registerReceiver(serviceReceiver, filter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {

                    if (!articleList.isEmpty()) {
                        Intent intent = new Intent();
                        intent.setAction(ACTION_NEWS_STORY);
                        intent.putExtra("articleList",articleList);
                        sendBroadcast(intent);

                        articleList.clear();
                    }

                }
            }
        }).start();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(serviceReceiver);
        isRunning=false;
        super.onDestroy();
    }

    public void setArticles(ArrayList<Article> articleList) {
        if(!articleList.isEmpty()){
            this.articleList.clear();
            this.articleList = articleList;
        }
    }

    public class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(ACTION_MSG_TO_SERVICE)){
                String sourceId = intent.getStringExtra("SOURCE_OBJECT");
                new NewsArticleDownloader(NewsService.this).execute(sourceId);
            }
        }
    }
}

