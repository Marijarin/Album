package ru.netology.album.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.album.R
import ru.netology.album.databinding.CardSongBinding
import ru.netology.album.dto.Song


interface OnInteractionListener {
    fun onPlay(song: Song) {}
    fun onPause(song: Song) {}
}

class SongsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Song, SongViewHolder>(SongDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding =
            CardSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = getItem(position)
        holder.bind(song)

    }
}

class SongViewHolder(
    private val binding: CardSongBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(song: Song) {
        binding.apply {
            name.text = song.file
            album.text = song.album
            play.isVisible = !song.playing
            pause.isVisible = song.playing
            play.setOnClickListener {
                onInteractionListener.onPlay(song)
            }

            pause.setOnClickListener {
                onInteractionListener.onPause(song)
            }


        }
    }

}

class SongDiffCallBack : DiffUtil.ItemCallback<Song>() {
    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem == newItem
    }

}