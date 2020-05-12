package com.abc.kzvideodownloader.utils

import android.content.Context
import android.graphics.Typeface
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity


class Utils{

    object connection{
        fun verifyAvailableNetwork(activity: AppCompatActivity):Boolean{
            val connectivityManager=activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo=connectivityManager.activeNetworkInfo
            return  networkInfo!=null && networkInfo.isConnected
        }
    }

    object fontsetter{
        fun getMMTypeface(context: Context): Typeface {
            return Typeface.createFromAsset(context.getAssets(), "font/jost.ttf")
        }
    }





}