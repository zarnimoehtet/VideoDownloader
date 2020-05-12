package com.abc.kzvideodownloader.utils

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.abc.kzvideodownloader.R
import com.abc.kzvideodownloader.tasks.downloadFile
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd

class CustomDialog{
    object getdiago{
        fun showDialog(link: String, title: String, c: Context) {

            var mInterstitialAd : InterstitialAd = InterstitialAd(c)
            mInterstitialAd.adUnitId = c.getString(R.string.AdmobInterstitial)
            mInterstitialAd.loadAd(AdRequest.Builder().addTestDevice("22FA3A9362C0D69F08D6DFB5EE1A3A74").build())
            val dialog = Dialog(c)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.custom_dialog)
            val body = dialog.findViewById(R.id.text_msg) as TextView
            body.text = "Do you want to download this " + title + ".mp4 file?"
            val yesBtn = dialog.findViewById(R.id.download) as Button
            val noBtn = dialog.findViewById(R.id.cancel) as Button
            val close = dialog.findViewById(R.id.dia_close) as ImageView
            yesBtn.setOnClickListener {
                downloadFile.Downloading(c, link, title, ".mp4")
                dialog.dismiss()
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.")
                }
            }
            noBtn.setOnClickListener {
                dialog.dismiss()
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.")
                }
            }

            close.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()

        }
    }
}