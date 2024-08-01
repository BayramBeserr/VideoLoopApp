package com.example.videoloopapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var alarmManager: AlarmManager
    private lateinit var restartIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoView = findViewById(R.id.videoView)
        val videoPath = "android.resource://" + packageName + "/" + R.raw.video
        val uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)

        videoView.setOnCompletionListener {
            videoView.start() // Video bittiğinde tekrar başla
        }

        videoView.start() // Uygulama açıldığında video başla

        // AlarmManager ve PendingIntent'i başlat
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, RestartReceiver::class.java)
        restartIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    override fun onPause() {
        super.onPause()
        videoView.pause() // Uygulama arka plana gittiğinde video durdurulur

        // AlarmManager kullanarak 15 dakika sonra uygulamayı yeniden başlat
        val triggerTime = System.currentTimeMillis() + (10000) // 15 dakika
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, restartIntent)
    }

    override fun onResume() {
        super.onResume()
        videoView.start() // Uygulama geri döndüğünde video başlar

        // AlarmManager'ı iptal et
        alarmManager.cancel(restartIntent)
    }
}
