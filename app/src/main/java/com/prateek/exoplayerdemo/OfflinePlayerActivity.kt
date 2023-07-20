package com.prateek.exoplayerdemo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.media3.common.MediaItem
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.offline.DownloadHelper
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.prateek.exoplayerdemo.databinding.ActivityMainBinding
import com.prateek.exoplayerdemo.databinding.ActivityOfflinePlayerBinding
import com.prateek.exoplayerdemo.manager.DemoUtil
import com.prateek.exoplayerdemo.manager.DownloadTracker
import java.io.File

class OfflinePlayerActivity : AppCompatActivity(), Player.Listener {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityOfflinePlayerBinding.inflate(layoutInflater)
    }


    private var player: ExoPlayer? = null
    private var playbackPosition = 0L
    private var playWhenReady = true
//     var downloadCache: Cache? = null


    private var mediaItemIndex = 0
//    private lateinit var binding: ActivityMainBinding
    private var downloadTracker: DownloadTracker? = null

//    companion object {
//        const val VIDEO_URL =
//            "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"
//
//        fun getIntent(context: Context): Intent {
//            return Intent(context, OfflinePlayerActivity::class.java)
//        }
//
//    }

    companion object {
        const val VIDEO_URL =

//            // audio
//            "https://firebasestorage.googleapis.com/v0/b/bg-audio-video-image.appspot.com/o/audio%2F05.%20Space%20Cadet%20(feat.%20Gunna).mp3?alt=media&token=15c6fd1a-f4b7-4bfe-ba98-acec4b674383"

//         video
        "https://firebasestorage.googleapis.com/v0/b/bg-audio-video-image.appspot.com/o/video%2FVIDEO%20GHI.mp4?alt=media&token=3a0598a0-823f-4999-b3aa-50707c8336c9"

//        https://firebasestorage.googleapis.com/v0/b/bg-audio-video-image.appspot.com/o/audio%2F05.%20Space%20Cadet%20(feat.%20Gunna).mp3?alt=media&token=15c6fd1a-f4b7-4bfe-ba98-acec4b674383

        fun getIntent(context: Context): Intent {
            return Intent(context, OfflinePlayerActivity::class.java)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        downloadTracker = DemoUtil.getDownloadTracker(this)
        binding.download.visibility = View.GONE
        binding.playOffline.visibility = View.GONE
    }

    private fun initPlayer() {

        val upstreamFactory =
            DefaultDataSource.Factory(this, DemoUtil.getHttpDataSourceFactory(this)!!)

        player =
            ExoPlayer.Builder(this)
                .setMediaSourceFactory(
                    DefaultMediaSourceFactory(this).setDataSourceFactory( buildReadOnlyCacheDataSource(upstreamFactory,  DemoUtil.getDownloadCache(this)))
                )
                .build()
                .also { exoPlayer ->
                    binding.playerExo.player = exoPlayer

//        player = ExoPlayer.Builder(this).build()
                    exoPlayer.playWhenReady = true
//                    binding.playerExo.player = player

                    val mediaSource = downloadTracker?.getDownloadRequest(Uri.parse(VIDEO_URL))!!.let {
                        DownloadHelper.createMediaSource(
                            it,
                            DemoUtil.getDataSourceFactory(this)!!
                        )

//                DownloadHelper.createMediaSource(
//                    it,
//                    DemoUtil.getDataSourceFactory(this)
//                )
                    }



                    // Update setMediaItems to include secondMediaItem
                    exoPlayer.setMediaItems(listOf(mediaSource.mediaItem), mediaItemIndex, playbackPosition)
                    exoPlayer.seekTo(playbackPosition)
                    exoPlayer.playWhenReady = playWhenReady
                    exoPlayer.prepare()

                }



    }


    fun buildReadOnlyCacheDataSource(
        upstreamFactory: DataSource.Factory, cache: Cache?
    ): CacheDataSource.Factory {
        return CacheDataSource.Factory()
            .setCache(cache!!)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setCacheWriteDataSinkFactory(null)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    private fun releasePlayer() {
        player?.let {player ->
            playbackPosition = player.currentPosition
            mediaItemIndex = player.currentMediaItemIndex
            playWhenReady = player.playWhenReady
//            player.removeListener(playbackStateListener)

            player.release()
        }
        player = null
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