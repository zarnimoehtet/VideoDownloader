package com.abc.videodownloader.tasks;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.abc.videodownloader.MainActivity;
import com.abc.videodownloader.R;
import com.abc.videodownloader.utils.iUtils;

import java.io.File;


import static com.abc.videodownloader.utils.Constants.DOWNLOAD_DIRECTORY;
import static com.abc.videodownloader.utils.Constants.PREF_APPNAME;


public class downloadFile {
    public static DownloadManager downloadManager;
    public static long downloadID;
    private static String mBaseFolderPath;




    public static void Downloading(Context context, String url, String title, String ext) {
        String cutTitle = title;
        String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
        cutTitle = cutTitle.replaceAll(characterFilter, "");
        cutTitle = cutTitle.replaceAll("['+.^:,#\"]", "");
        cutTitle = cutTitle.replace(" ", "-").replace("!", "").replace(":", "") + ext;
        if (cutTitle.length() > 100)
            cutTitle = cutTitle.substring(0, 100) + ext;
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(title);
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


        String folderName = DOWNLOAD_DIRECTORY;
        SharedPreferences preferences = context.getSharedPreferences(PREF_APPNAME, Context.MODE_PRIVATE);

        if (!preferences.getString("path", "DEFAULT").equals("DEFAULT")) {

            mBaseFolderPath = preferences.getString("path", "DEFAULT");
        } else {

            mBaseFolderPath = android.os.Environment.getExternalStorageDirectory() + File.separator + folderName;
        }
        String[] bits = mBaseFolderPath.split("/");
        String Dir = bits[bits.length - 1];
        //  request.setDestinationUri(new File(mBaseFolderPath).);
        request.setDestinationInExternalPublicDir(Dir, cutTitle);
        request.allowScanningByMediaScanner();
        downloadID = downloadManager.enqueue(request);
        Log.e("downloadFileName", cutTitle);
        iUtils.ShowToast(context, "Downloading Start!");

    }


}
