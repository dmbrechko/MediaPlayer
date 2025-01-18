package com.example.mediaplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mediaplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null
    private val songs = listOf(R.raw.s1, R.raw.s2, R.raw.s3, R.raw.s4)
    private val handler = Handler(Looper.getMainLooper())
    private val handlerToken = "handler token"
    private var current = 0
    private var lastPlayed = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.apply {
            playIV.setOnClickListener {
                playSong()
            }
            pauseIV.setOnClickListener {
                mediaPlayer?.pause()
            }
            stopIV.setOnClickListener {
                mediaPlayer?.let { player ->
                    player.stop()
                    player.reset()
                    player.release()
                }
                mediaPlayer = null
            }
            prevIV.setOnClickListener {
                if(--current < 0) songs.lastIndex else current
                playSong()
            }
            nextIV.setOnClickListener {
                current = ++current % songs.size
                playSong()
            }
            progressSB.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) mediaPlayer?.seekTo(progress)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }

            })
            volumeSB.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) mediaPlayer?.setVolume(progress.toFloat() / 100, progress.toFloat() / 100)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }

            })
        }
    }

    private fun playSong() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, songs[current])
            lastPlayed = current
        } else {
            if(current != lastPlayed) {
                mediaPlayer?.let { player ->
                    player.stop()
                    player.reset()
                    player.release()
                }
                mediaPlayer = MediaPlayer.create(this, songs[current])
                lastPlayed = current
            }
        }
        binding.apply {
            mediaPlayer?.setVolume(volumeSB.progress.toFloat() / 100, volumeSB.progress.toFloat() / 100)
            mediaPlayer?.start()
            mediaPlayer?.setOnCompletionListener {
                current = ++current % songs.size
                playSong()
            }
            progressSB.max = mediaPlayer!!.duration
            handler.removeCallbacksAndMessages(handlerToken)
            handler.postDelayed(object: Runnable {
                override fun run() {
                    try {
                        progressSB.progress = mediaPlayer!!.currentPosition
                        handler.postDelayed(this, handlerToken, 1000)
                    } catch (e: Exception) {
                        progressSB.progress = 0
                    }
                }

            }, handlerToken,0)
        }

    }

}