package com.prateek.exoplayerdemo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import com.prateek.exoplayerdemo.databinding.ActivityMainBinding
import com.prateek.exoplayerdemo.databinding.ActivityOnlinePlayerBinding
import com.prateek.exoplayerdemo.manager.DemoUtil
import com.prateek.exoplayerdemo.manager.DownloadTracker

class OnlinePlayerActivity : AppCompatActivity(), Player.Listener {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityOnlinePlayerBinding.inflate(layoutInflater)
    }

    private var player: ExoPlayer? = null
    private var playbackPosition = 0L
    private var playWhenReady = true


    private var mediaItemIndex = 0
//    private lateinit var binding: ActivityMainBinding
    private var downloadTracker: DownloadTracker? = null

    private val mediaItem by lazy {
        MediaItem.Builder()
            .setUri(VIDEO_URL)
            .setMediaId("dummyId")
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle("Demo Video")
                    .build()
            )
            .build()
    }

    companion object {
        const val VIDEO_URL =
//            // audio
//            "https://firebasestorage.googleapis.com/v0/b/bg-audio-video-image.appspot.com/o/audio%2F05.%20Space%20Cadet%20(feat.%20Gunna).mp3?alt=media&token=15c6fd1a-f4b7-4bfe-ba98-acec4b674383"

//        video
            "https://firebasestorage.googleapis.com/v0/b/bg-audio-video-image.appspot.com/o/video%2FVIDEO%20GHI.mp4?alt=media&token=3a0598a0-823f-4999-b3aa-50707c8336c9"

    //            streaming
    //            "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"
    }

//    private val mediaItem by lazy {
//        MediaItem.Builder()
//            .setUri(VIDEO_URL)
//            .setMediaId("dummyId")
//            .setMediaMetadata(
//                MediaMetadata.Builder()
//                    .setTitle("Demo Video")
//                    .build()
//            )
//            .build()
//    }
//
//    companion object {
//        const val VIDEO_URL =
//            "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        downloadTracker = DemoUtil.getDownloadTracker(this)
        binding.download.setOnClickListener {
            val renderersFactory = DemoUtil.buildRenderersFactory(this)
            downloadTracker?.toggleDownload(
                supportFragmentManager,
                mediaItem,
                renderersFactory
            )
        }
        binding.playOffline.setOnClickListener {
            if (downloadTracker?.isDownloaded(mediaItem) == true) {
                startActivity(OfflinePlayerActivity.getIntent(this))
            } else {
                Toast.makeText(this, "Video Not Downloaded yet", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initPlayer() {
        player = ExoPlayer.Builder(this).build()
        player?.playWhenReady = true
        binding.playerExo.player = player


//        val defaultHttpDataSourceFactory = DemoUtil.getDataSourceFactory(this)
//        val mediaSource =
//            HlsMediaSource.Factory(defaultHttpDataSourceFactory!!).createMediaSource(mediaItem)
//        player?.setMediaSource(mediaSource)

        val mediaItem = MediaItem.fromUri(VIDEO_URL)

        // Update setMediaItems to include secondMediaItem
        player?.setMediaItems(listOf(mediaItem), mediaItemIndex, playbackPosition)


        player?.seekTo(playbackPosition)
        player?.playWhenReady = playWhenReady
        player?.prepare()

    }

    private fun releasePlayer() {
        player?.let {
            playbackPosition = it.currentPosition
            playWhenReady = it.playWhenReady
            mediaItemIndex = it.currentMediaItemIndex

            it.release()
            player = null
        }
    }



    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24) {
            initPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }
}