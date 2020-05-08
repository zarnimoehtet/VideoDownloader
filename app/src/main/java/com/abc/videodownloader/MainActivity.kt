package com.abc.videodownloader

import android.app.*
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.*
import android.database.Cursor
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.abc.videodownloader.tasks.downloadFile

import com.abc.videodownloader.tasks.downloadFile.downloadID
import com.abc.videodownloader.tasks.downloadVideo
import com.abc.videodownloader.utils.Utils
import com.abc.videodownloader.utils.iUtils
import com.google.android.gms.ads.*

import com.roacult.backdrop.BackdropLayout
import com.zcy.pudding.Pudding

class MainActivity : AppCompatActivity() {



    private var NotifyID = 1001;

    private var csRunning = false;

    lateinit var prefEditor: SharedPreferences.Editor;
    lateinit var pref: SharedPreferences;
    lateinit var backdropLayout: BackdropLayout
    val REQUEST_PERMISSION_CODE = 1001
    val REQUEST_PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE

    var clearcheck: Boolean = false

    var clip: String = ""

    lateinit var urlinput: EditText


    private lateinit var mInterstitialAd: InterstitialAd
    lateinit var mAdView: AdView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this)
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.AdmobInterstitial)
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        // checkPermission()


        pref = this.getSharedPreferences("tikVideoDownloader", 0) // 0 - for private mode
        prefEditor = pref.edit()
        csRunning = pref.getBoolean("csRunning", false)
        urlinput = findViewById(R.id.etURL)

        if (csRunning) {
            val clipBoardManager =
                this@MainActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            val primaryClipData = clipBoardManager.primaryClip
            clip = primaryClipData!!.getItemAt(0).text.toString()

            urlinput.text = Editable.Factory.getInstance().newEditable(clip)
            clearcheck = true
            Log.e("CSRunning ==>", "Running")
        } else {

            clearcheck = false
        }

        doinOncreate()


        mAdView.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.e("AdLoadeed","AdLoaded")


            }

            override fun onAdFailedToLoad(errorCode : Int) {
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

    fun doinOncreate() {


        backdropLayout = findViewById(R.id.container)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)


        val appname: TextView = findViewById(R.id.appname)
        val desc: TextView = findViewById(R.id.description)
        val btn: CardView = findViewById(R.id.download_btn)
        val dtext: TextView = findViewById(R.id.downloadText)

        val ivlink: ImageView = findViewById(R.id.ivLink)


        if (clearcheck) {
            ivlink.setImageDrawable(getDrawable(R.drawable.close))
        } else {
            ivlink.setImageDrawable(getDrawable(R.drawable.ic_link))
        }

        ivlink.setOnClickListener {
            if (clearcheck) {
                urlinput.setText("")
                ivlink.setImageDrawable(getDrawable(R.drawable.ic_link))
                clearcheck = false
            } else {

                    val clipBoardManager =
                        this@MainActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                    val primaryClipData = clipBoardManager.primaryClip
                    clip = primaryClipData!!.getItemAt(0).text.toString()

                    urlinput.text = Editable.Factory.getInstance().newEditable(clip)
                    ivlink.setImageDrawable(getDrawable(R.drawable.close))
                    clearcheck = true

            }
        }



        backdropLayout.setOnBackdropChangeStateListener {
            when (it) {
                BackdropLayout.State.OPEN -> {

                    btn.setClickable(true)

                }
                BackdropLayout.State.CLOSE -> {

                    btn.setClickable(false)
                }
            }
        }



        appname.setTypeface(Utils.fontsetter.getMMTypeface(this))
        desc.setTypeface(Utils.fontsetter.getMMTypeface(this))
        dtext.setTypeface(Utils.fontsetter.getMMTypeface(this))
        createNotificationChannel(
            this@MainActivity,
            NotificationManagerCompat.IMPORTANCE_LOW,
            true,
            getString(R.string.app_name),
            "The Best VideoDownloader"
        )

        changeFragment(FrontFragment())

        btn.setOnClickListener {
            val url = urlinput.text.toString()
            DownloadVideo(url)

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

//            // 2
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

            var builder = NotificationCompat.Builder(this, "${context.packageName}-$name")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(name)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(importance,builder.build())
            Log.e("loged", "Notificaion Channel Created!")
        }
    }

    fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }

    fun DownloadVideo(url: String) {




        if (url.equals("") && !!iUtils.checkURL(url)) {
            Pudding.create(this@MainActivity) {
                setTitle("Error")
                setText("Please enter a valid URL")
                setChocoBackgroundColor(resources.getColor(R.color.red))
                setIcon(R.drawable.ic_warning_black_24dp)
            }.show()
        } else {

            if(Utils.connection.verifyAvailableNetwork(this@MainActivity)){
                if(url.contains("tiktok") || url.contains("instagram")){
                    showDialog(url)
                }else{
                    downloadVideo.Start(this, url!!, false,this@MainActivity)
                }

            }else{
                Pudding.create(this@MainActivity) {
                    setTitle("Failure")
                    setText("No network connection")
                    setChocoBackgroundColor(resources.getColor(R.color.red))
                    setIcon(R.drawable.ic_warning_black_24dp)
                }.show()
            }


        }
    }

    fun showDialog(link: String) {

        val dialog = Dialog(this@MainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)
        val body = dialog.findViewById(R.id.text_msg) as TextView
        body.text = "Do you want to download this video file?"
        val yesBtn = dialog.findViewById(R.id.download) as Button
        val noBtn = dialog.findViewById(R.id.cancel) as Button
        val close = dialog.findViewById(R.id.dia_close) as ImageView
        yesBtn.setOnClickListener {
            downloadVideo.Start(this, link!!, false,this@MainActivity)
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {

            backdropLayout.open()

        }
    }


//    private fun checkPermission() {
//
//
//        if (ContextCompat.checkSelfPermission(
//                this,
//                REQUEST_PERMISSION
//            ) == PackageManager.PERMISSION_DENIED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(REQUEST_PERMISSION),
//                REQUEST_PERMISSION_CODE
//            )
//
//        }
//
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        try {
//            if (requestCode == REQUEST_PERMISSION_CODE) {
//                if (grantResults != null && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                } else {
//                    //iUtils.ShowToast(this@MainActivity, getString(R.string.info_permission_denied))
//                    Pudding.create(this@MainActivity) {
//                        setTitle("Failure")
//                        setText(getString(R.string.info_permission_denied))
//                        setChocoBackgroundColor(resources.getColor(R.color.red))
//                        setIcon(R.drawable.ic_warning_black_24dp)
//                    }.show()
//
//                    finish()
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            // iUtils.ShowToast(this@MainActivity, getString(R.string.info_permission_denied))
//            Pudding.create(this@MainActivity) {
//                setTitle("Failure")
//                setText(getString(R.string.info_permission_denied))
//                setChocoBackgroundColor(resources.getColor(R.color.red))
//                setIcon(R.drawable.ic_warning_black_24dp)
//            }.show()
//
//            finish()
//        }
//
//    }


//    fun startClipboardMonitor() {
//        prefEditor.putBoolean("csRunning", true);
//        prefEditor.commit()
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val service = this.startForegroundService(
//                Intent(
//                    this,
//                    ClipboardMonitor::class.java
//                ).setAction(STARTFOREGROUND_ACTION)
//            );
//        } else {
//            val service = this.startService(
//                Intent(
//                    this,
//                    ClipboardMonitor::class.java
//                )
//            )
//        }
//
//    }
//
//    fun stopClipboardMonitor() {
//        prefEditor.putBoolean("csRunning", false);
//        prefEditor.commit()
//
//        val service = this.stopService(
//            Intent(
//                this,
//                ClipboardMonitor::class.java
//            ).setAction(STOPFOREGROUND_ACTION)
//        )
//
//
//    }
//
//    fun makePendingIntent(name: String): PendingIntent {
//        val intent = Intent(this, Receiver::class.java)
//        intent.setAction(name)
//        return PendingIntent.getBroadcast(this, 0, intent, 0)
//    }

    private val onDownloadComplete = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //Fetching the download id received with the broadcast
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {



                Pudding.create(this@MainActivity) {
                    setTitle("Success")
                    setText("Your download is completed")
                    setChocoBackgroundColor(resources.getColor(R.color.green))
                    setIcon(R.drawable.ic_check)
                }.show()


                backdropLayout.setOnBackdropChangeStateListener {
                    when (it) {
                        BackdropLayout.State.OPEN -> {
                            changeFragment(FrontFragment())

                        }
                        BackdropLayout.State.CLOSE -> {
                            changeFragment(FrontFragment())

                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        backdropLayout.setOnBackdropChangeStateListener {
            when (it) {
                BackdropLayout.State.OPEN -> {
                    changeFragment(FrontFragment())

                }
                BackdropLayout.State.CLOSE -> {
                    changeFragment(FrontFragment())

                }
            }
        }
    }


    override fun onStop() {
        super.onStop()
        unregisterReceiver(onDownloadComplete)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        backdropLayout.setOnBackdropChangeStateListener {
            when (it) {
                BackdropLayout.State.OPEN -> {
                    changeFragment(FrontFragment())

                }
                BackdropLayout.State.CLOSE -> {
                    changeFragment(FrontFragment())

                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.setting -> {
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


}
