package com.abc.videodownloader

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.abc.videodownloader.services.ClipboardMonitor
import com.abc.videodownloader.utils.Constants.STARTFOREGROUND_ACTION
import com.abc.videodownloader.utils.Constants.STOPFOREGROUND_ACTION
import com.abc.videodownloader.utils.Utils

import android.graphics.Color
import androidx.appcompat.app.AlertDialog
import com.marcoscg.licenser.License
import com.marcoscg.licenser.Library
import com.marcoscg.licenser.LicenserDialog
import com.google.android.gms.ads.*


class SettingsActivity : AppCompatActivity() {

    private var csRunning = false

    lateinit var prefEditor: SharedPreferences.Editor
    lateinit var pref: SharedPreferences

    lateinit var switcher: Switch
    lateinit var mAdView : AdView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)




        pref = this.getSharedPreferences("tikVideoDownloader", 0)// 0 - for private mode
        prefEditor = pref.edit()
        csRunning = pref.getBoolean("csRunning", false)
        Log.e("csRunning", csRunning.toString())

        MobileAds.initialize(this)
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        val appname: TextView = findViewById(R.id.app_name)
        val version: TextView = findViewById(R.id.version)
        val setting: TextView = findViewById(R.id.settingtitle)
        val tuto_text: TextView = findViewById(R.id.tuto_text)
        val cpy_text: TextView = findViewById(R.id.cpy_text)
        val ores_text: TextView = findViewById(R.id.ores_text)
        val cpylayout: RelativeLayout = findViewById(R.id.instantcopy)
        val tutorial: RelativeLayout = findViewById(R.id.tutorial)
        val opensource: RelativeLayout = findViewById(R.id.opensource)

        switcher = findViewById(R.id.cpy_switch)

        appname.typeface = Utils.fontsetter.getMMTypeface(this)
        version.typeface = Utils.fontsetter.getMMTypeface(this)
        setting.typeface = Utils.fontsetter.getMMTypeface(this)
        tuto_text.typeface = Utils.fontsetter.getMMTypeface(this)
        cpy_text.typeface = Utils.fontsetter.getMMTypeface(this)
        ores_text.typeface = Utils.fontsetter.getMMTypeface(this)

        if (csRunning) {

            Log.e("CSRunning ==>", "Running")
        } else {

            Log.e("CSRunning ==>", "not Running")
        }

        if (csRunning) {
            //  view.chkAutoDownload.isChecked=true;
            switcher.isChecked = true
            startClipboardMonitor()
        } else {
            //   view.chkAutoDownload.isChecked=false;
            switcher.isChecked = false
            stopClipboardMonitor()
        }


        cpylayout.setOnClickListener {
            val checked = switcher.isChecked

            if (checked) {
                switcher.isChecked = false
                stopClipboardMonitor()
                //     Toast.makeText( this@SettingsActivity,"False",Toast.LENGTH_SHORT).show()
            } else {


                val builder = AlertDialog.Builder(this@SettingsActivity)
                builder.setTitle("Warning!!")
                builder.setMessage("This Instant Copy function is not support on Android 9!")
                //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

                builder.setPositiveButton("Know it!") { dialog, which ->
                  dialog.dismiss()
                    switcher.isChecked = true
                    startClipboardMonitor()
                }

                builder.show()

                //    Toast.makeText( this@SettingsActivity,"True",Toast.LENGTH_SHORT).show()
            }
        }

        tutorial.setOnClickListener {


            startActivity(Intent(this@SettingsActivity, GuideActivity::class.java))

        }

        opensource.setOnClickListener {
            LicenserDialog(this)
                .setTitle("Licenses")
                .setCustomNoticeTitle("Notices for files:")
                .setBackgroundColor(Color.GRAY) // Optional
                .setLibrary(
                    Library(
                        "Android Support Libraries",
                        "https://developer.android.com/topic/libraries/support-library/index.html",
                        License.APACHE2
                    )
                ) // APACHE deprecated, see wiki
                .setLibrary(
                    Library(
                        "BackdropLayout",
                        "https://github.com/roiacult/BackdropLayout",
                        License.APACHE2
                    )
                )
                .setLibrary(
                    Library(
                        "Pudding",
                        "https://github.com/o0o0oo00/Pudding",
                        License.APACHE2
                    )
                )
                .setLibrary(
                    Library(
                        "Jsoup",
                        "https://github.com/jhy/jsoup",
                        License.APACHE2
                    )
                )
                .setLibrary(
                    Library(
                        "Glide",
                        "https://github.com/bumptech/glide",
                        License.APACHE2
                    )
                )
                .setLibrary(
                    Library(
                        "OkHttp",
                        "https://github.com/square/okhttp",
                        License.APACHE2
                    )
                )
                .setLibrary(
                    Library(
                        "Gson",
                        "https://github.com/google/gson",
                        License.APACHE2
                    )
                )// APACHE deprecated, see wiki
                .setLibrary(
                    Library(
                        "Licenser",
                        "https://github.com/marcoscgdev/Licenser",
                        License.MIT
                    )
                )
                .setPositiveButton(
                    android.R.string.ok
                ) { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                .show()
        }


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


    fun startClipboardMonitor() {
        prefEditor.putBoolean("csRunning", true)
        prefEditor.commit()
        Log.e("csRunning", "start")

        Log.e("csRunningLog", pref.getBoolean("csRunning", false).toString())


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val service =  this@SettingsActivity.startForegroundService(
                Intent(
                    this,
                    ClipboardMonitor::class.java
                ).setAction(STARTFOREGROUND_ACTION)
            )


        } else {
            val service =   this@SettingsActivity.startService(
                Intent(
                    this,
                    ClipboardMonitor::class.java
                )
            )
        }

    }

    fun stopClipboardMonitor() {
        prefEditor.putBoolean("csRunning", false)
        prefEditor.commit()
        Log.e("csRunning", "stop")

        Log.e("csRunningLog", pref.getBoolean("csRunning", false).toString())

        val service =   this@SettingsActivity.stopService(
            Intent(
                this,
                ClipboardMonitor::class.java
            ).setAction(STOPFOREGROUND_ACTION)
        )


    }
}
