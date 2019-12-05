package orionedutech.`in`.lmstrainerapp.fragments.mainCourse


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.fragment_video.view.*
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.interfaces.ShowActivityViews
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast


/**
 * A simple [Fragment] subclass.
 */
class VideoFragment : Fragment(), Player.EventListener{
    private lateinit var playerV : PlayerView
    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private lateinit var playbackStateListener : PlaybackStateListener
    lateinit var buffering : LottieAnimationView
    private var mediaUrl = ""
    lateinit var showActivityViews : ShowActivityViews

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
        playbackStateListener = PlaybackStateListener()
        val bundle = arguments
        playerV = view.playerView
        buffering = view.lottie
        mediaUrl = bundle!!.getString("url")!!
        view.button.setOnClickListener {
        showActivityViews.show(true)
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
        val uri = Uri.parse(mediaUrl)
        val mediaSource = buildMediaSource(uri)
        player!!.addListener(playbackStateListener)
        player!!.playWhenReady = playWhenReady
        player!!.seekTo(currentWindow, playbackPosition)
        player!!.prepare(mediaSource, false, false)
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
            playWhenReady = player!!.playWhenReady
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            player!!.release()
            player = null
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
}
