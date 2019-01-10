package com.example.sharan.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String ACTION_NEWS_STORY="ACTION_NEWS_STORY";
    private static String ACTION_MSG_TO_SERVICE="ACTION_MSG_TO_SERVICE";

    private MyPageAdapter pageAdapter;
    private List<Fragment> fragments;
    private ViewPager pager;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private NewsReceiver newsReceiver;
    ListAdapter listAdapter;
    private ArrayList<String> items = new ArrayList<>();
    private HashMap<String,Source> srchashMap ;
    private ArrayList categoryList = new ArrayList();
    Menu menuList;
    private ArrayList<Article> currentlyLoadedArticles = new ArrayList<>();
    private int currentArticleNumber = -1, currentSourcePosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start NewService service
        Intent intent = new Intent(MainActivity.this, NewsService.class);
        startService(intent);

        newsReceiver=new NewsReceiver();
        IntentFilter filter1 = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, filter1);

        //Drawer and View Pager setup
        mDrawerLayout =  findViewById(R.id.drawer_layout);
        mDrawerList =  findViewById(R.id.drawer);

        listAdapter = new ArrayAdapter<>(this, R.layout.drawer_list, items);
        mDrawerList.setAdapter(listAdapter);
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
                //Store position for orientation change retrieval
                currentSourcePosition = position;
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewPager);
        pager.setAdapter(pageAdapter);

        srchashMap = new HashMap<>();


        if(savedInstanceState == null)
            new NewsSourceDownloader(this).execute("");
    }


    private void selectItem(int position) {
        pager.setBackground(null);
        setTitle(items.get(position));
        Intent intent = new Intent();
        intent.setAction(ACTION_MSG_TO_SERVICE);
        Source src = srchashMap.get(items.get(position));
        intent.putExtra("SOURCE_OBJECT",src.getId());
        sendBroadcast(intent);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void updateFragments(ArrayList<Article> list) {
        int size = list.size();

        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);
        fragments.clear();
        for (int i = 0; i < list.size(); i++) {
            Article a = list.get(i);
            fragments.add(MyFragment.newInstance(this,a,i,size));
        }
        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("category", categoryList);
        outState.putSerializable("source", items);
        outState.putSerializable("sourceHashMap", srchashMap);
        outState.putSerializable("currentlyLoadedArticles", currentlyLoadedArticles);
        outState.putInt("articleNumber", currentArticleNumber);
        outState.putInt("sourceNumber", currentSourcePosition);

        // Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);

        categoryList = (ArrayList) savedInstanceState.getSerializable("category");

        items = (ArrayList) savedInstanceState.getSerializable("source");
        srchashMap = (HashMap) savedInstanceState.getSerializable("sourceHashMap");
        currentSourcePosition = savedInstanceState.getInt("sourceNumber");
        if(items != null) {
            listAdapter = new ArrayAdapter<>(this, R.layout.drawer_list, items);
            mDrawerList.setAdapter(listAdapter);
        }

        currentlyLoadedArticles = (ArrayList<Article>) savedInstanceState.getSerializable("currentlyLoadedArticles");
        currentArticleNumber = savedInstanceState.getInt("articleNumber");

        if(currentlyLoadedArticles != null && currentArticleNumber != -1 && currentSourcePosition != -1){

            pager.setBackground(null);
            setTitle(items.get(currentSourcePosition));

            updateFragments(currentlyLoadedArticles);
            pager.setCurrentItem(currentArticleNumber-1);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuList=menu;
        getMenuInflater().inflate(R.menu.menu, menu);

        if(categoryList != null && categoryList.size() > 0){
            for(int j=0;j<categoryList.size();j++){
                menuList.add(categoryList.get(j).toString());
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        //Category selected from Options Menu
        new NewsSourceDownloader(this).execute(item.toString());
        return super.onOptionsItemSelected(item);
    }

    public void setSources(List<Source> sourceList, List<String> catList) {

        if(!sourceList.isEmpty() ){
            srchashMap.clear();
            items.clear();

            for (int i=0;i<sourceList.size();i++){
                Source  s= sourceList.get(i);
                items.add(s.getName());
                srchashMap.put(s.getName(),s);
            }
        }

        if(catList.isEmpty()){
            categoryList.clear();
            categoryList.add("all");

        }
        else if(categoryList.isEmpty()){
            categoryList = (ArrayList) catList;
            categoryList.add("all");
        }

        else if(!catList.isEmpty())
        {
            for (int k=0;k<catList.size();k++){
                if (!categoryList.contains(catList.get(k))){
                    categoryList.add(catList.get(k));
                }
            }
        }
        if(menuList != null){
            menuList.clear();
            for(int j=0;j<categoryList.size();j++){
                menuList.add(categoryList.get(j).toString());
            }

        }
        listAdapter = new ArrayAdapter<>(this, R.layout.drawer_list, items);
        mDrawerList.setAdapter(listAdapter);
    }

    public void openPicassoPhoto(String url, ImageView im) {
        final ImageView imageView = im;
        final String photUrl = url;
        if (url != null) {
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    // Here we try https if the http image attempt failed
                    final String changedUrl = photUrl.replace("http:", "https:");
                    picasso.load(changedUrl)
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(imageView);
                }
            }).build();
            picasso.load(url)
                    .resize(360,1060)
                    .centerInside()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);

        } else {
            Picasso.get().load(url)
                    .error(R.drawable.brokenimage)
                    .into(imageView);
        }
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            currentArticleNumber = position;
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            return baseId + position;
        }

        public void notifyChangeInPosition(int n) {
            baseId += getCount() + n;
        }

    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<>();
        return fList;
    }

    public class NewsReceiver  extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("ACTION_NEWS_STORY")){
                ArrayList<Article> articles =  (ArrayList<Article>) intent.getSerializableExtra("articleList");
                updateFragments(articles);
                currentlyLoadedArticles = articles;
            }
        }
    }

    @Override
    protected void onDestroy() {
        Intent intent1 = new Intent(MainActivity.this, NewsService.class);
        stopService(intent1);
        unregisterReceiver(newsReceiver);
        super.onDestroy();
    }
}