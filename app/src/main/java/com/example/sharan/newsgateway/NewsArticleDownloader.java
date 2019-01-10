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

public class NewsArticleDownloader  extends AsyncTask<String, Void, String> {

    private NewsService ns;
    private String source;
    private ArrayList<Article> articleList = new ArrayList<Article>();
    private String API_KEY = "48e5f56e5b8048659006bdc2d4680cbd";
    private String URL = "http://newsapi.org/v1/articles";

    public NewsArticleDownloader(NewsService ns) {
        this.ns = ns;
    }

    @Override
    protected String doInBackground(String[] params) {
        source =params[0];
        Uri.Builder buildURL = Uri.parse(URL).buildUpon();
        buildURL.appendQueryParameter("source", source);
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
        return sb.toString().replace("\n//","");
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONObject object = new JSONObject(s);
            JSONArray articles = (JSONArray) object.get("articles");
            for (int i=0; i < articles.length(); i++){
                JSONObject jObj = (JSONObject) articles.get(i);
                String author = jObj.getString("author");
                String title = jObj.getString("title");
                String desc = jObj.getString("description");
                String urlArticle = jObj.getString("url");
                String urlImage = jObj.getString("urlToImage");
                String publish = jObj.getString("publishedAt");
                articleList.add(new Article(author,title,desc,urlArticle,urlImage,publish));
            }
            if(!articleList.isEmpty())
                ns.setArticles(articleList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}