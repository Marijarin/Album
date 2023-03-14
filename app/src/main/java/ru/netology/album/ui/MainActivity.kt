package ru.netology.album.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
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
        setContentView(view)
        lifecycle.addObserver(mediaObserver)

        binding.pause.visibility = View.INVISIBLE

        val adapter = SongsAdapter(object : OnInteractionListener {
            override fun onPlay(song: Song) {
                viewModel.playSong(song)
                binding.play.visibility = View.GONE
                binding.pause.visibility = View.VISIBLE
            }

            override fun onPause(song: Song) {
                viewModel.pauseSong()
                binding.pause.visibility = View.GONE
                binding.play.visibility = View.VISIBLE
            }
        })
        binding.list.adapter = adapter
        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest(adapter::submitList)
            binding.album.text = viewModel.loadAlbum()
                .map { it.title }
                .last()
                .toString()
            binding.artist.text = viewModel.loadAlbum()
                .map { it.artist }
                .last()
                .toString()
            binding.genre.text = viewModel.loadAlbum()
                .map { it.genre }
                .last()
                .toString()
            binding.releaseYear.text = viewModel.loadAlbum()
                .map { it.published }
                .last()
                .toString()

        }


        binding.play.setOnClickListener {
            binding.pause.visibility = View.VISIBLE
            it.visibility = View.GONE

            viewModel.playSong(adapter.currentList.first())
        }

        binding.pause.setOnClickListener {
            it.visibility = View.GONE
            binding.play.visibility = View.VISIBLE
            viewModel.pauseSong()
        }
    }
}