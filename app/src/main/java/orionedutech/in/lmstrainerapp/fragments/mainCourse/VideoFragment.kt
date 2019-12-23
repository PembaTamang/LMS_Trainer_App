package orionedutech.`in`.lmstrainerapp.fragments.mainCourse


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.airbnb.lottie.LottieAnimationView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.custom_player_layout.view.*
import kotlinx.android.synthetic.main.fragment_video.view.*
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.activities.TrainerActivity
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast.showToast
import orionedutech.`in`.lmstrainerapp.network.*
import java.util.*
import kotlin.concurrent.fixedRateTimer

/**
 * A simple [Fragment] subclass.
 */
class VideoFragment : Fragment() {
    private lateinit var playerV: PlayerView
    private lateinit var player: ExoPlayer
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private lateinit var playbackStateListener: PlaybackStateListener
    lateinit var buffering: LottieAnimationView
    private var mediaUrl = ""
    private var chapterid = ""
    lateinit var mute: ImageButton
    private var muteAudio = false
    private var currentAudioLevel = 0f
    private lateinit var controls: PlayerControlView
    private lateinit var timer: Timer
    private lateinit var currentTime: TextView
    lateinit var screenName: TextView
    lateinit var totalTime: TextView
    lateinit var fullScreen: ImageButton
    lateinit var startActivity: MaterialButton
    var full = false
    var isTimerRunning = false
    var ended = false
    private lateinit var fullscreendialog: Dialog
    lateinit var fullScreenPlayerView: PlayerView
    lateinit var videoPref: SharedPreferences
    var trainingID = ""
    var trainerID = ""
    var centerID = ""
    var batchID = ""
    var courseID = ""
    var storageID = " "
    var moduleID = ""
    var unitID = ""
    var subUnitID = ""
    var startTime: Long = 0L
    var once = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity!!.window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        val view = inflater.inflate(R.layout.fragment_video, container, false)

        val bundle = arguments
        playerV = view.playerView
        playerV.defaultArtwork = ContextCompat.getDrawable(context!!, R.drawable.exo_icon_play)
        buffering = view.lottie
        screenName = view.heading

        mediaUrl = bundle!!.getString("url")!!
        screenName.text = bundle.getString("name")!!
        chapterid = bundle.getString("chapter_id")!!
        trainingID = bundle.getString("training_id")!!
        trainerID = bundle.getString("user_id")!!
        centerID = bundle.getString("center_id")!!
        batchID = bundle.getString("batch_id")!!
        courseID = bundle.getString("course_id")!!
        storageID = bundle.getString("storage_id")!!
        moduleID = bundle.getString("module_id")!!
        unitID = bundle.getString("unit_id")!!
        subUnitID = bundle.getString("subunit_id")!!
        mLog.i(TAG, " $mediaUrl")
        // mediaUrl = "https://orionedutech.co.in/cglms_real/uploads/courses/units/lessons/6CMTdz7txu.mp4"
        videoPref = activity!!.getSharedPreferences("videoPref", Context.MODE_PRIVATE)

        controls = view.controlview
        currentTime = controls.current_time
        totalTime = controls.total_time
        mute = controls.mute
        fullScreen = controls.full
        startActivity = view.button

        startActivity.setOnClickListener {
            //go to activity
            val intent = Intent(context, TrainerActivity::class.java)
            intent.putExtra("chapter_id", chapterid)
            startActivity(intent)
            //activity!!.supportFragmentManager.popBackStack()
            activity!!.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)

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
                player.audioComponent!!.volume =
                    if (currentAudioLevel == 0f) 50f else currentAudioLevel
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
            if (!full) {
                full = true
                activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                fullScreenPlayerView = PlayerView(context)
                val fullvideoContainer = FrameLayout(context!!)
                fullscreendialog =
                    object : Dialog(context!!, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
                        override fun onBackPressed() {
                            activity!!.requestedOrientation =
                                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                            PlayerView.switchTargetView(player, fullScreenPlayerView, playerV)
                            full = false
                            super.onBackPressed()
                        }
                    }
                fullScreenPlayerView.setControlDispatcher(PositionLimitingControlDispatcher())
                fullvideoContainer.addView(fullScreenPlayerView)
                fullscreendialog.addContentView(
                    fullvideoContainer,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
                fullscreendialog.show()
                PlayerView.switchTargetView(player, playerV, fullScreenPlayerView)

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

    override fun onDestroy() {
        super.onDestroy()
        mLog.i(TAG, "adding bookmark")
        videoPref.edit().putLong(mediaUrl, player.contentPosition).apply()
        activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        sendUsageData()
    }

    private fun sendUsageData() {
        val json = JSONObject()
        json.put("training_id", trainingID)
        json.put("user_id", trainerID)
        json.put("user_type", "3")
        json.put("center_id", centerID)
        json.put("batch_id", batchID)
        json.put("current_course_id", courseID)
        json.put("current_chapter_id", chapterid)
        json.put("current_storage_id", storageID)
        json.put("module_id", moduleID)
        json.put("media_seek", (player.contentPosition / 1000).toString())
        json.put(
            "media_seek_remain",
            ((player.duration - player.currentPosition) / 1000).toString()
        )
        json.put("media_duration", (player.duration / 1000).toString())
        json.put("is_book_marked", if (ended) "2" else "1")
        json.put("current_unit_id", unitID)
        json.put("current_sub_unit_id", subUnitID)
        json.put("media_type", "2")

        val builder = Data.Builder()
        builder.putString("json", json.toString())
        val dataRequest = OneTimeWorkRequestBuilder<UsageData>()
            .setInputData(builder.build())
            .build()
        val workManager = WorkManager.getInstance(context!!)
        workManager.enqueue(dataRequest)

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
            videoPref.edit().putLong(mediaUrl, player.contentPosition).apply()
            player.release()
            if (isTimerRunning) {
                timer.cancel()
                isTimerRunning = false
                once = false
            }

        }
    }

    inner class PlaybackStateListener : Player.EventListener {
        override fun onPlayerError(error: ExoPlaybackException?) {
            super.onPlayerError(error)
            mLog.i(TAG, "on error")
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
            MaterialAlertDialogBuilder(context).setTitle("An error has occured")
                .setMessage("Error details : $errorString")
                .setCancelable(false)
                .setPositiveButton("Go back") { dialogInterface, i ->
                    dialogInterface.dismiss()
                    activity!!.onBackPressed()
                }.create().show()
        }

        override fun onPlayerStateChanged(
            playWhenReady: Boolean,
            playbackState: Int
        ) {
            val stateString: String
            when (playbackState) {
                ExoPlayer.STATE_IDLE -> {

                    stateString = "ExoPlayer.STATE_IDLE      -"
                    buffering.visibility = View.INVISIBLE
                    startActivity.isEnabled = false
                }
                ExoPlayer.STATE_BUFFERING -> {
                    stateString = "ExoPlayer.STATE_BUFFERING -"
                    buffering.setAnimation("loading.json")
                    buffering.visibility = View.VISIBLE
                    buffering.playAnimation()
                    startActivity.isEnabled = false
                }
                ExoPlayer.STATE_READY -> {
                    stateString = "ExoPlayer.STATE_READY     -"
                    startActivity.isEnabled = false
                    buffering.visibility = View.INVISIBLE
                    buffering.cancelAnimation()
                    if (!isTimerRunning) {
                        runTimer()
                        isTimerRunning = true
                    }
                    if (!once) {
                        startTime = System.currentTimeMillis()
                        player.seekTo(videoPref.getLong(mediaUrl, 0))
                        once = true
                    }
                }
                ExoPlayer.STATE_ENDED -> {
                    stateString = "ExoPlayer.STATE_ENDED     -"
                    buffering.visibility = View.INVISIBLE
                    buffering.cancelAnimation()
                    startActivity.isEnabled = true
                    ended = true

                }
                else -> stateString = "UNKNOWN_STATE             -"
            }
            mLog.i(
                TAG, "changed state to " + stateString
                        + " playWhenReady: " + playWhenReady
            )
        }
    }

    fun runTimer() {
        timer = fixedRateTimer("timer", false, 0, 1000) {
            if (activity == null) {
                return@fixedRateTimer
            }
            activity!!.runOnUiThread {
                updateUI()
            }
        }
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
