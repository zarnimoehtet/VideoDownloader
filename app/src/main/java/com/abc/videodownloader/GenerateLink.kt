package com.abc.videodownloader

import android.app.Dialog
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.opengl.Visibility
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.abc.videodownloader.tasks.downloadFile
import com.abc.videodownloader.utils.Utils
import com.roacult.backdrop.BackdropLayout
import com.zcy.pudding.Pudding
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.UnifiedNativeAd


class GenerateLink : AppCompatActivity() {

    private lateinit var mInterstitialAd: InterstitialAd
    lateinit var mAdView: AdView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_link)


        val lsd: String = intent.getStringExtra("sd")
        val lhd: String = intent.getStringExtra("hd")
        val t: String = intent.getStringExtra("title")

        val cd = ColorDrawable(-0x999a)

        Log.e("SD", lsd)

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.AdmobInterstitial)
        mInterstitialAd.loadAd(AdRequest.Builder().build())


        MobileAds.initialize(this)
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        val title: TextView = findViewById(R.id.name)
        val name: TextView = findViewById(R.id.name)
        val input: EditText = findViewById(R.id.link)
        val hd: Button = findViewById(R.id.dHD)
        val sd: Button = findViewById(R.id.dSD)



        title.typeface = Utils.fontsetter.getMMTypeface(this)
        name.typeface = Utils.fontsetter.getMMTypeface(this)
        input.typeface = Utils.fontsetter.getMMTypeface(this)
        hd.typeface = Utils.fontsetter.getMMTypeface(this)
        sd.typeface = Utils.fontsetter.getMMTypeface(this)

        input.text = Editable.Factory.getInstance().newEditable(t)


        if (lhd.equals("false")) {
            hd.visibility = GONE
        } else if (lsd.equals("false")) {
            sd.visibility = GONE
        }

        hd.setOnClickListener {
            val tit = input.text.toString()


            showDialog(lhd, tit)


        }

        sd.setOnClickListener {
            val tit = input.text.toString()



            showDialog(lsd, tit)
        }

        createNotificationChannel(
            this@GenerateLink,
            NotificationManagerCompat.IMPORTANCE_LOW,
            true,
            getString(R.string.app_name),
            "The Best VideoDownloader"
        )


//        MobileAds.initialize(this, "[_app-id_]")
//        val adLoader = AdLoader.Builder(this, "[_ad-unit-id_]")
//            .forUnifiedNativeAd { unifiedNativeAd ->
//                val styles =
//                    NativeTemplateStyle.Builder().withMainBackgroundColor(cd).build()
//
//                val template : TemplateView = findViewById(R.id.my_template)
//                template.setStyles(styles)
//                template.setNativeAd(unifiedNativeAd)
//            }
//            .build()
//
//        adLoader.loadAd(AdRequest.Builder().build())

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

        val dialog = Dialog(this@GenerateLink)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)
        val body = dialog.findViewById(R.id.text_msg) as TextView
        body.text = "Do you want to download this " + title + ".mp4 file?"
        val yesBtn = dialog.findViewById(R.id.download) as Button
        val noBtn = dialog.findViewById(R.id.cancel) as Button
        val close = dialog.findViewById(R.id.dia_close) as ImageView
        yesBtn.setOnClickListener {
            downloadFile.Downloading(this@GenerateLink, link, title, ".mp4")
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


                Pudding.create(this@GenerateLink) {
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

            // 2
//            val channelId = "${context.packageName}-$name"
//            val channel = NotificationChannel(channelId, name, importance)
//            channel.description = description
//
//            channel.setShowBadge(showBadge)
//
//            // 3
//            val notificationManager: NotificationManager =
//                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//            Log.e("loged", "Notificaion Channel Created!")

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
