package com.abc.kzvideodownloader

import android.app.Dialog
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.abc.kzvideodownloader.tasks.downloadFile
import com.abc.kzvideodownloader.utils.Utils
import com.google.android.gms.ads.*
import com.zcy.pudding.Pudding

class YouTubeDownLoad : AppCompatActivity() {

    private lateinit var mInterstitialAd: InterstitialAd
    lateinit var mAdView: AdView
    lateinit var t : String
    lateinit var p360: String
    lateinit var p480: String
    lateinit var p720: String
    lateinit var p1080: String
    lateinit var edt: EditText
    lateinit var one: Button
    lateinit var two: Button
    lateinit var three: Button
    lateinit var four: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube)


        t = intent.getStringExtra("title")
        p360 = intent.getStringExtra("360p")
        p480 = intent.getStringExtra("480p")
        p720 = intent.getStringExtra("720p")
        p1080 = intent.getStringExtra("1080p")

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.AdmobInterstitial)
        mInterstitialAd.loadAd(AdRequest.Builder().addTestDevice("22FA3A9362C0D69F08D6DFB5EE1A3A74").build())


        MobileAds.initialize(this)
        mAdView = findViewById(R.id.adView)
        val adRequest =
            AdRequest.Builder().addTestDevice("22FA3A9362C0D69F08D6DFB5EE1A3A74").build()
        mAdView.loadAd(adRequest)

        val title: TextView = findViewById(R.id.name)
        val name: TextView = findViewById(R.id.name)
        edt = findViewById(R.id.link)
        one = findViewById(R.id.p360)
        two = findViewById(R.id.p480)
        three = findViewById(R.id.p720)
        four = findViewById(R.id.p1080)

        title.typeface = Utils.fontsetter.getMMTypeface(this)
        name.typeface = Utils.fontsetter.getMMTypeface(this)
        edt.typeface = Utils.fontsetter.getMMTypeface(this)
        one.typeface = Utils.fontsetter.getMMTypeface(this)
        two.typeface = Utils.fontsetter.getMMTypeface(this)
        three.typeface = Utils.fontsetter.getMMTypeface(this)
        four.typeface = Utils.fontsetter.getMMTypeface(this)

        edt.text = Editable.Factory.getInstance().newEditable(t)

        if (p1080 == "false") {
            four.visibility = GONE
        }

        if (p360 == "false") {
            one.visibility = GONE
        }

        if (p480 == "false") {
            two.visibility = GONE
        }

        if (p720 == "false") {
            three.visibility = GONE
        }

        one.setOnClickListener { showDialog(p360, t) }


        two.setOnClickListener { showDialog(p480, t) }

        three.setOnClickListener { showDialog(p720, t) }

        four.setOnClickListener { showDialog(p1080, t) }




        createNotificationChannel(
            this@YouTubeDownLoad,
            NotificationManagerCompat.IMPORTANCE_LOW,
            true,
            getString(R.string.app_name),
            "The Best VideoDownloader"
        )

        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.e("AdLoadeed", "AdLoaded")


            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }

    }

     fun showDialog(link: String, title: String) {

        val dialog = Dialog(this@YouTubeDownLoad)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)
        val body = dialog.findViewById(R.id.text_msg) as TextView
        body.text = "Do you want to download this " + title + ".mp4 file?"
        val yesBtn = dialog.findViewById(R.id.download) as Button
        val noBtn = dialog.findViewById(R.id.cancel) as Button
        val close = dialog.findViewById(R.id.dia_close) as ImageView
        yesBtn.setOnClickListener {
            downloadFile.Downloading(this@YouTubeDownLoad, link, title, ".mp4")
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


    private val onDownloadComplete = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //Fetching the download id received with the broadcast
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadFile.downloadID == id) {


                Pudding.create(this@YouTubeDownLoad) {
                    setTitle("Success")
                    setText("Your download is completed")
                    setChocoBackgroundColor(resources.getColor(R.color.green))
                    setIcon(R.drawable.ic_check)
                }.show()


            }
        }
    }


    fun createNotificationChannel(
        context: Context,
        importance: Int,
        showBadge: Boolean,
        name: String,
        description: String
    ) {
        // 1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {



            var builder = NotificationCompat.Builder(this, "${context.packageName}-$name")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(name)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(importance, builder.build())
        }
    }


    override fun onStart() {
        super.onStart()
        registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }


    override fun onStop() {
        super.onStop()
        unregisterReceiver(onDownloadComplete)
    }
}

