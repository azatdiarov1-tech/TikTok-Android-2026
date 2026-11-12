
package com.videoflow.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.ui.viewinterop.AndroidView

private val Dark = Color(0xFF09090B)
private val Pink = Color(0xFFFF285A)
private val Cyan = Color(0xFF25F4EE)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = Dark,
                    surface = Dark,
                    primary = Pink,
                    secondary = Cyan
                )
            ) {
                VideoFlowApp(
                    openTikTok = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.tiktok.com/")
                        )
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun VideoFlowApp(openTikTok: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val videos = remember { mutableStateListOf<Uri>() }
    var selected by remember { mutableStateOf<Uri?>(null) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { results ->
        results.forEach { uri ->
            if (!videos.contains(uri)) {
                videos.add(uri)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Dark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "VideoFlow",
                color = Color.White,
                fontSize = 25.sp
            )
            Text(
                "2026",
                color = Cyan,
                fontSize = 14.sp
            )
        }

        if (selected != null) {
            val uri = selected!!
            key(uri) {
                val player = remember(uri) {
                    ExoPlayer.Builder(context).build().apply {
                        setMediaItem(MediaItem.fromUri(uri))
                        prepare()
                        playWhenReady = true
                    }
                }

                DisposableEffect(player) {
                    onDispose { player.release() }
                }

                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            this.player = player
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Выбери видео для просмотра",
                    color = Color.LightGray
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 180.dp)
        ) {
            items(videos) { uri ->
                Text(
                    text = uri.lastPathSegment ?: "Видео",
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selected = uri }
                        .padding(12.dp),
                    maxLines = 1
                )
            }
        }

        Button(
            onClick = { picker.launch(arrayOf("video/*")) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Pink
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Добавить видео с телефона")
        }

        OutlinedButton(
            onClick = openTikTok,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text("Открыть официальный TikTok")
        }

        Spacer(Modifier.height(12.dp))
    }
}
