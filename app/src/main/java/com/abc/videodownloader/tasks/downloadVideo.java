package com.abc.videodownloader.tasks;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.abc.videodownloader.GenerateLink;
import com.abc.videodownloader.MainActivity;
import com.abc.videodownloader.R;
import com.abc.videodownloader.WebViewActivity;
import com.abc.videodownloader.WelcomeActivity;
import com.abc.videodownloader.model.LinkModel;
import com.abc.videodownloader.utils.iUtils;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import static android.content.ContentValues.TAG;
import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.abc.videodownloader.utils.Constants.DOWNLOADING_MSG;
import static com.abc.videodownloader.utils.Constants.WEB_DISABLE;
import static com.abc.videodownloader.utils.Constants.WENT_WRONG;


public class downloadVideo {

    public static Context Mcontext;
    public static ProgressDialog pd;
    public static Dialog dialog;
    static String SessionID, Title;
    static int error=1;
    public static SharedPreferences prefs;

    public static Boolean fromService;
    public static String msg = "";
    private static String VideoURL;
    private static String VideoTitle;
    private static String BASE_URL = "https://kzviedodownloader.000webhostapp.com/downloader.php";
    private static OkHttpClient client = new OkHttpClient();
    private static Activity activity;



    public static void Start(final Context context , String url , Boolean service , Activity activity){

        Mcontext=context;
        fromService = service;
        activity = activity;

        if(!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        if(!fromService) {
            pd = new ProgressDialog(context);
            pd.setMessage(DOWNLOADING_MSG);
            pd.setCancelable(false);
            pd.show();
        }
        if(url.contains("tiktok.com")){

//          new GetTikTokVideo().execute(url);
            new Data().execute(getVideoId(url));
        } else if (url.contains("facebook.com")){


           new GetFacebookVideo().execute(url);
            getLink(url);
        }else if (url.contains("instagram.com")){

            new GetInstagramVideo().execute(url);
        }else{
            if(!fromService) {
                pd.dismiss();

                iUtils.ShowToast(Mcontext,WEB_DISABLE);
            }
        }


        prefs = Mcontext.getSharedPreferences("AppConfig", MODE_PRIVATE);


    }

    public static String getVideoId(String link) {
        if(!link.contains("https"))
        {
            link = link.replace("http","https");
        }
        return link;
    }


    public static void getLink(final String code) {



            final RequestBody body = new FormBody.Builder()
                    .add("url", code)
                    .build();

            final Request request = new Request.Builder().url(BASE_URL).post(body).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("Registration Error" + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response)throws IOException {
                    try {

                        String resp = response.body().string();
                        Log.e("RESPONSE", resp);



                        JsonObject jsonObject = new JsonParser().parse(resp).getAsJsonObject();


                        final String HD = jsonObject.get("HDLink").getAsString();
                        String SD = jsonObject.get("SDLink").getAsString();
                        String title = jsonObject.get("Title").getAsString();

                        StringTokenizer st = new StringTokenizer(SD,",");
                        SD = st.nextToken();

                        Log.e("HD", HD);
                        Log.e("SD",SD);

                        if(HD.equals("false") && SD.contains("false")){

                            //TODO check private video download

                            Intent i = new Intent(Mcontext, WebViewActivity.class);
                            i.putExtra("id",code);
                            Mcontext.startActivity(i);

                        }else if(HD.equals("false")){

                            Intent i = new Intent(Mcontext, GenerateLink.class);
                            i.putExtra("hd",HD);
                            i.putExtra("sd",SD.substring(1,SD.length()-1));
                            i.putExtra("title",title);
                            Mcontext.startActivity(i);
                            pd.dismiss();

                        }else if(SD.contains("false")){
                            Intent i = new Intent(Mcontext, GenerateLink.class);
                            i.putExtra("hd",HD);
                            i.putExtra("sd","false");
                            i.putExtra("title",title);
                            Mcontext.startActivity(i);
                            pd.dismiss();
                        }else{
                            Intent i = new Intent(Mcontext, GenerateLink.class);
                            i.putExtra("hd",HD);
                            i.putExtra("sd",SD.substring(1,SD.length()-1));
                            i.putExtra("title",title);
                            Mcontext.startActivity(i);
                        }


                    } catch (JsonIOException j) {
                        System.out.println("JSONException caught" + j.getMessage());
                    }
                }

            });


    }

//    public static void showDialog(final String hd, final String sd){
//        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
////        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
//        final View dialogView = LayoutInflater.from(Mcontext).inflate(R.layout.custom_dialog, null, false);
//        builder.setView(dialogView);
//        final AlertDialog alertDialog = builder.create();
//        ImageView img = dialogView.findViewById(R.id.dia_close);
//        img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDialog.dismiss();
//            }
//        });
//
//        Button DHD = dialogView.findViewById(R.id.dHD);
//        DHD.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new downloadFile().Downloading(Mcontext,hd,Title,".mp4");
//            }
//        });
//
//        Button DSD = dialogView.findViewById(R.id.dSD);
//        DSD.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new downloadFile().Downloading(Mcontext,sd,Title,".mp4");
//            }
//        });
//        alertDialog.show();
//    }


    //---------------

    private static class GetFacebookVideo extends AsyncTask<String, Void, Document> {
        Document doc;

        @Override
        protected Document doInBackground(String... urls) {
            try {

                doc = Jsoup.connect(urls[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: Error");
                iUtils.ShowToast(Mcontext,WENT_WRONG);

            }
            return doc;

        }

        protected void onPostExecute(Document result) {
            if(!fromService) {

                pd.dismiss();
            }

            try {
                //String URL = result.select("meta[property=\"og:video\"]").last().attr("content");
                Title = result.title();


                //new downloadFile().Downloading(Mcontext,URL,Title,".mp4");
            } catch (NullPointerException e)
            {
                e.printStackTrace();
                iUtils.ShowToast(Mcontext,WENT_WRONG);
            }

        }

    }



    //----------------------

    private static class GetInstagramVideo extends AsyncTask<String, Void, Document> {
        Document doc;

        @Override
        protected Document doInBackground(String... urls) {
            try {
                doc = Jsoup.connect(urls[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: Error");
            }
            return doc;

        }

        protected void onPostExecute(Document result) {
            if(!fromService) {

                pd.dismiss();}
             Log.d("GetResult", result.toString() );
            try {
                String URL = result.select("meta[property=\"og:video\"]").last().attr("content");
                Title = result.title();
                iUtils.ShowToast(Mcontext, URL);

                new downloadFile().Downloading(Mcontext, URL, Title, ".mp4");
            }catch (NullPointerException e)
            {
                e.printStackTrace();
                iUtils.ShowToast(Mcontext,WENT_WRONG);
            }
        }
    }




    private static class Data extends AsyncTask<String, String,String>{

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection;
            BufferedReader reader;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer =new StringBuilder();
                String Line;
                while ((Line = reader.readLine()) != null)
                {
                    //Log.e("Hello", Line);
                    if(Line.contains("videoData"))
                    {
                        Line = Line.substring(Line.indexOf("videoData"));
                        //Log.e("Hello",Line);
                        Line = Line.substring(Line.indexOf("urls"));
                        //Log.e("Hello",Line);
                        VideoTitle = Line.substring(Line.indexOf("text"));
                        if(VideoTitle.contains("#")) {
                            VideoTitle = VideoTitle.substring(ordinalIndexOf(VideoTitle, "\"", 1) + 1, ordinalIndexOf(VideoTitle, "#", 0));
                        }
                        else {
                            VideoTitle = VideoTitle.substring(ordinalIndexOf(VideoTitle, "\"", 1) + 1, ordinalIndexOf(VideoTitle, "\"", 2));
                        }
                        //Log.e("HelloTitle",VideoTitle);
                        Line = Line.substring(ordinalIndexOf(Line,"\"",1)+1,ordinalIndexOf(Line,"\"",2));
                        //Log.e("HelloURL",Line);
                        if(!Line.contains("https"))
                        {
                            Line = Line.replace("http","https");
                        }
                        buffer.append(Line);
                        break;
                    }
                }
                return buffer.toString();
            } catch (IOException e) {
                return "Invalid Video URL or Check Internet Connection";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if(!fromService) {

                pd.dismiss();
            }
            if(URLUtil.isValidUrl(s))
            {

                try {
                    new downloadFile().Downloading(Mcontext,s,VideoTitle,".mp4");
                } catch (Exception e) {
                    if (Looper.myLooper()==null)
                        Looper.prepare();
                    Toast.makeText(Mcontext, "Video Can't be downloaded! Try Again", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
            else {
                if (Looper.myLooper()==null)
                    Looper.prepare();
                Toast.makeText(Mcontext, s, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
    }
    private static int ordinalIndexOf(String str, String substr, int n) {
        int pos = -1;
        do {
            pos = str.indexOf(substr, pos + 1);
        } while (n-- > 0 && pos != -1);
        return pos;
    }

}




