package ru.netology.album.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message

import android.view.View
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.internal.http2.Http2Reader
import ru.netology.album.adapter.OnInteractionListener
import ru.netology.album.adapter.SongsAdapter
import ru.netology.album.databinding.ActivityMainBinding
import ru.netology.album.dto.Song
import ru.netology.album.viewmodel.AlbumViewModel

class MainActivity : AppCompatActivity() {
    private val mediaObserver = MediaLifecycleObserver()
    private val viewModel: AlbumViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        binding.pauseL.visibility = View.INVISIBLE
        setContentView(view)
        lifecycle.addObserver(mediaObserver)
        mediaObserver.apply {
            player?.isLooping
        }
        val adapter = SongsAdapter(object : OnInteractionListener {
            override fun onPlay(song: Song) {
                if (viewModel.data.filter { album -> album?.tracks?.any { it.playing } == true }
                        .toString().isNotEmpty()) {
                    viewModel.pauseSong()
                    viewModel.playSong(song)
                } else
                    viewModel.playSong(song)
            }

            override fun onPause(song: Song) {
                viewModel.pauseSong(song)

            }
        })
        binding.list.adapter = adapter
        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                binding.album.text = it?.title
                binding.artist.text = it?.artist
                binding.genre.text = it?.genre
                binding.releaseYear.text = it?.published
                adapter.submitList(it?.tracks)
            }


        }
        if (mediaObserver.player?.isPlaying == true) {
            binding.seekBar.max = mediaObserver.player?.duration!!
        }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    mediaObserver.player?.seekTo(progress * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        val msg = Message()
        msg.what = mediaObserver.player?.currentPosition!!
        while (mediaObserver.player?.isPlaying == true) {
            binding.seekBar.handler.postDelayed(object : Runnable {
                override fun run() {
                    try {
                        binding.seekBar.handler.sendMessage(msg)
                        binding.seekBar.progress = mediaObserver.player?.currentPosition!!

                    } catch (e: Exception) {
                        binding.seekBar.progress = 0
                    }
                }
            }, 0)

            binding.seekBar.handler.handleMessage(msg)
        }

        binding.playL.setOnClickListener {
            it.visibility = View.GONE
            binding.pauseL.visibility = View.VISIBLE
            viewModel.playSong(adapter.currentList.first())

        }
        binding.pauseL.setOnClickListener {
            it.visibility = View.GONE
            binding.playL.visibility = View.VISIBLE
            viewModel.pauseSong()
        }

    }
}