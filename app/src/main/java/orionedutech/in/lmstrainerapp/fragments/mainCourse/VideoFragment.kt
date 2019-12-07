package orionedutech.`in`.lmstrainerapp.fragments.mainCourse


import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.custom_player_layout.view.*
import kotlinx.android.synthetic.main.fragment_video.view.*
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.interfaces.ShowActivityViews
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast
import java.util.*
import kotlin.concurrent.fixedRateTimer

/**
 * A simple [Fragment] subclass.
 */
class VideoFragment : Fragment(), Player.EventListener{
    private lateinit var playerV : PlayerView
    private lateinit var player: ExoPlayer
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private lateinit var playbackStateListener : PlaybackStateListener
    lateinit var buffering : LottieAnimationView
    private var mediaUrl = ""
    private lateinit var showActivityViews : ShowActivityViews
    lateinit var mute: ImageButton
    private var muteAudio = false
    private var currentAudioLevel = 0f
    private lateinit var controls: PlayerControlView
    private lateinit var timer: Timer
    private lateinit var currentTime : TextView
    lateinit var screenName : TextView
    lateinit var totalTime : TextView
    lateinit var fullScreen : ImageButton
    var full = false
    override fun onAttach(context: Context) {
        super.onAttach(context)
       showActivityViews = context as ShowActivityViews
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_video, container, false)

        val bundle = arguments
        playerV = view.playerView
        playerV.defaultArtwork  = ContextCompat.getDrawable(context!!,R.drawable.exo_icon_play)
        buffering = view.lottie
        screenName = view.heading
        mediaUrl = bundle!!.getString("url")!!
        screenName.text = bundle.getString("name")!!
        controls = view.controlview
        currentTime = controls.current_time
        totalTime = controls.total_time
        currentTime.text = "time"
        totalTime.text = "mtime"
        mute = controls.mute
        fullScreen = controls.full

        view.button.setOnClickListener {
        showActivityViews.show(true)
        }

        playbackStateListener = PlaybackStateListener()
        currentAudioLevel = 0f

        mute.setOnClickListener {
            if (!muteAudio) {
                player.audioComponent!!.volume = 0f
                mute.setImageDrawable(
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.ic_volume_off
                    )
                )
                muteAudio = true
            } else {
                player.audioComponent!!.volume = if (currentAudioLevel == 0f) 50f else currentAudioLevel
                mute.setImageDrawable(
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.ic_volume_on
                    )
                )
                muteAudio = false
            }
        }
        fullScreen.setOnClickListener {
            if(!full){
                full = true
                activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                val fullScreenPlayerView = PlayerView(context)
                val dialog = object : Dialog(context!!, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
                    override fun onBackPressed() {
                        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        PlayerView.switchTargetView(player, fullScreenPlayerView, playerV)
                        super.onBackPressed()
                    }
                }
                fullScreenPlayerView.setControlDispatcher(PositionLimitingControlDispatcher())
                dialog.addContentView(
                    fullScreenPlayerView,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
                dialog.show()
                PlayerView.switchTargetView(player, playerV, fullScreenPlayerView)

            }else{
                full  = false
            }

        }
        return view

    }
    private fun buildMediaSource(uri: Uri): MediaSource? {
        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(context, "exoplayer-codelab")
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(uri)
    }

    private fun initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(context)
        playerV.player = player
        controls.player = player
        val uri = Uri.parse(mediaUrl)
        currentAudioLevel = player.audioComponent!!.volume
        val mediaSource = buildMediaSource(uri)
        player.addListener(playbackStateListener)
        player.playWhenReady = playWhenReady
        controls.setControlDispatcher(PositionLimitingControlDispatcher())
        player.prepare(mediaSource, false, false)
        timer = fixedRateTimer("timer",false,0,1000){
            activity!!.runOnUiThread {
                updateUI()
            }
        }
    }
    private fun updateUI() {
        if (player != null) {
            val current = player.contentPosition
            val total = player.duration
            currentTime.text = String.format(
                Locale.getDefault(),
                "%02d:%02d",
                current / 1000 / 60,
                (current / 1000 % 60).toInt()
            )
            totalTime.text = String.format(
                Locale.getDefault(),
                "%02d:%02d",
                total / 1000 / 60,
                (total / 1000 % 60).toInt()
            )
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        //  hideSystemUi();
        if (Util.SDK_INT < 24 || player == null) {
            initializePlayer()
        }
    }
    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            playWhenReady = player.playWhenReady
            playbackPosition = player.currentPosition
            currentWindow = player.currentWindowIndex
            player.release()
            timer.cancel()
        }
    }
  inner class PlaybackStateListener : Player.EventListener {
        override fun onPlayerStateChanged(
            playWhenReady: Boolean,
            playbackState: Int
        ) {
            val stateString: String
            when (playbackState) {
                ExoPlayer.STATE_IDLE -> {

                    stateString = "ExoPlayer.STATE_IDLE      -"
                    buffering.visibility = View.INVISIBLE
                }
                ExoPlayer.STATE_BUFFERING -> {
                    stateString = "ExoPlayer.STATE_BUFFERING -"
                    buffering.setAnimation("loading.json")
                    buffering.visibility = View.VISIBLE
                    buffering.playAnimation()
                }
                ExoPlayer.STATE_READY -> {
                    stateString = "ExoPlayer.STATE_READY     -"

                    buffering.visibility = View.INVISIBLE
                    buffering.cancelAnimation()

                }
                ExoPlayer.STATE_ENDED -> {
                    stateString = "ExoPlayer.STATE_ENDED     -"
                    buffering.visibility = View.INVISIBLE
                    buffering.cancelAnimation()
                }
                else -> stateString = "UNKNOWN_STATE             -"
            }
            mLog.i(
                TAG, "changed state to " + stateString
                        + " playWhenReady: " + playWhenReady
            )
        }
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        super.onPlayerError(error)
        var errorString = ""
        when (error!!.type) {
            ExoPlaybackException.TYPE_REMOTE -> {
                mLog.i(TAG, "TYPE_REMOTE: " + error.sourceException.message)
                errorString = "remote error"
            }
            ExoPlaybackException.TYPE_OUT_OF_MEMORY -> {
                mLog.i(TAG, "TYPE_MEMORY: " + error.sourceException.message)
                errorString = "out of memory error"
            }
            ExoPlaybackException.TYPE_SOURCE -> {
                mLog.i(TAG, "TYPE_SOURCE: " + error.sourceException.message)
                errorString = "source error"
            }
            ExoPlaybackException.TYPE_RENDERER -> {
                mLog.i(TAG, "TYPE_RENDERER: " + error.rendererException.message)
                errorString = "render error"
            }
            ExoPlaybackException.TYPE_UNEXPECTED -> {
                mLog.i(TAG, "TYPE_UNEXPECTED: " + error.unexpectedException.message)
                errorString = "unexpected"
            }
        }
        mToast.showToast(context, "$errorString has occurred")

    }
    private class PositionLimitingControlDispatcher :
        DefaultControlDispatcher() {
        private var maxPlayedPositionMs: Long = 0
        // Needs to be called from application code whenever a new playback starts.
        fun reset() {
            maxPlayedPositionMs = 0
        }

        // Note: This implementation assumes single window content. You might need to do
// something more complicated, depending on your use case.
        override fun dispatchSeekTo(
            player: Player,
            windowIndex: Int,
            positionMs: Long
        ): Boolean {
            mLog.i(TAG, "called")
            maxPlayedPositionMs =
                maxPlayedPositionMs.coerceAtLeast(player.currentPosition)
            player.seekTo(windowIndex, positionMs.coerceAtMost(maxPlayedPositionMs))
            return true
        }
    }
}
