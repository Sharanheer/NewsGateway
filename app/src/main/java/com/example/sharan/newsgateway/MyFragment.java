package com.example.sharan.newsgateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyFragment extends Fragment {

    TextView titleTextView, descTextView, authorNameTextView,articleDateTextView, positionTextView;
    ImageView imageView;
    static MainActivity ma;

    public static final MyFragment newInstance(MainActivity m, Article a, int pos, int size)
    {
        ma = m;
        MyFragment f = new MyFragment();
        Bundle bdl = new Bundle(1);
        bdl.putSerializable("Article", a);
        bdl.putInt("Position", pos);
        bdl.putInt("Total", size);
        f.setArguments(bdl);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final Article articles = (Article) getArguments().getSerializable("Article");
        int position  = getArguments().getInt("Position");
        int length  = getArguments().getInt("Total");


        View v = inflater.inflate(R.layout.fragment_layout, container, false);
        titleTextView=(TextView) v.findViewById(R.id.headline);
        descTextView=(TextView) v.findViewById(R.id.description);
        imageView = (ImageView)v.findViewById( R.id.image);
        authorNameTextView=(TextView) v.findViewById(R.id.authorName);
        articleDateTextView=(TextView) v.findViewById(R.id.articleDate);
        positionTextView=(TextView) v.findViewById(R.id.position);

        ma.openPicassoPhoto(articles.getUrlToImage(),imageView);

        if(articles.getTitle().equals("null") || articles.getTitle().isEmpty())
            titleTextView.setText("");
        else
            titleTextView.setText(articles.getTitle());

        if(articles.getDesc().equals("null") || articles.getDesc().isEmpty())
            descTextView.setText("");
        else
            descTextView.setText(articles.getDesc());


        if(articles.getAuthor().equals("null") || articles.getAuthor().isEmpty())
            authorNameTextView.setText("");
        else
            authorNameTextView.setText(articles.getAuthor());

        if(articles.getPublishDate().equals("null") || articles.getPublishDate().isEmpty())
            articleDateTextView.setText("");
        else {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
            SimpleDateFormat output = new SimpleDateFormat("MMM dd, yyyy HH:mm",Locale.ENGLISH);
            try{
                Date d = sdf.parse(articles.getPublishDate());
                String formattedTime = output.format(d);
                articleDateTextView.setText(formattedTime);
            }
            catch (ParseException e){
                e.printStackTrace();
            }

        }

        position=position+1;
        String pos = position +" of "+ length;
        positionTextView.setText(pos);


        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickNews(v, articles.getUrl());

            }
        });

        descTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickNews(v, articles.getUrl());

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickNews(v, articles.getUrl());

            }
        });

        return v;
    }
    public void clickNews(View v, String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}