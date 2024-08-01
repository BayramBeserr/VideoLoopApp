package com.example.aidata

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import android.widget.VideoView
import androidx.compose.ui.Alignment
import com.example.aidata.ui.theme.AidataTheme

class MainActivity : ComponentActivity() {
    private val inactivityTime = 5000L // 5 seconds
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable { restartApplication() }
    private var isUserInteracting = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AidataTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VideoScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }

        setupInactivityAlarm()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        if (!isUserInteracting) {
            resetInactivityAlarm()
        }
        isUserInteracting = true
        moveTaskToBack(true)
    }

    private fun setupInactivityAlarm() {
        handler.postDelayed(runnable, inactivityTime)
    }

    private fun resetInactivityAlarm() {
        handler.removeCallbacks(runnable)
        setupInactivityAlarm()
    }

    private fun restartApplication() {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    @Composable
    fun VideoScreen(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            VideoPlayer(
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }

    @Composable
    fun VideoPlayer(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val videoUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.video)
        val videoView = remember { VideoView(context).apply {
            setVideoURI(videoUri)
            setOnPreparedListener {
                it.isLooping = true
                start()
            }
            setOnCompletionListener {
                start()
            }
        } }

        AndroidView(
            modifier = modifier,
            factory = { videoView }
        )

        DisposableEffect(Unit) {
            onDispose {
                videoView.stopPlayback()
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun VideoScreenPreview() {
        AidataTheme {
            VideoScreen()
        }
    }
}
