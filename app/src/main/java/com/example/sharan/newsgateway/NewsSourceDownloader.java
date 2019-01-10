package com.example.sharan.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsSourceDownloader extends AsyncTask<String, Void, String> {

    private MainActivity ma;
    private String category;
    private List<String> categoryList = new ArrayList<String>();
    private List<Source> sourceList = new ArrayList<>();
    private String API_KEY = "48e5f56e5b8048659006bdc2d4680cbd";
    private String URL = "http://newsapi.org/v1/sources?language=en&country=us";

    public NewsSourceDownloader(MainActivity ma) {
        this.ma = ma;
    }

    @Override
    protected String doInBackground(String[] params) {
        if(params[0].isEmpty() || params[0].equals("all"))
            this.category = "";
        else
            this.category = params[0];
        Uri.Builder buildURL = Uri.parse(URL).buildUpon();
        buildURL.appendQueryParameter("category", category);
        buildURL.appendQueryParameter("apiKey", API_KEY);
        String urlToUse = buildURL.build().toString();
        StringBuilder sb = new StringBuilder();
        try {
            java.net.URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (Exception e) {
            return "Exception";
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONObject object = new JSONObject(s);
            JSONArray sources = new JSONArray(object.getString("sources"));
            for(int i=0;i<sources.length();i++){
                JSONObject source = (JSONObject) sources.get(i);
                String name =  source.getString("name");
                String id = source.getString("id");
                String category = source.getString("category");
                sourceList.add(new Source(id,name,category));
            }
            if (sourceList != null) {
                Source temp = sourceList.get(0);
                categoryList.add(temp.getCategory());

                for (int j = 1; j < sourceList.size(); j++) {
                    Source source = sourceList.get(j);
                    if (!categoryList.contains(source.getCategory())) {
                        categoryList.add(source.getCategory());
                    }
                }

                if (!categoryList.isEmpty() && !sourceList.isEmpty()) {
                    ma.setSources(sourceList, categoryList);
                }
            }

        }
        catch(JSONException e){
            e.printStackTrace();
        }

    }
}
