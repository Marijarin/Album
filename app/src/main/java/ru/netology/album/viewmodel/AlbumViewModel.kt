package ru.netology.album.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import okhttp3.internal.toImmutableList
import ru.netology.album.api.AlbumApi
import ru.netology.album.dto.Album
import ru.netology.album.dto.Song
import ru.netology.album.ui.MediaLifecycleObserver

class AlbumViewModel(application: Application) : AndroidViewModel(application) {
    private val mediaObserver = MediaLifecycleObserver()
    private val _data = MutableStateFlow<Album?>(null)
    val data: Flow<Album?> = _data.asStateFlow()

    companion object {
        private const val BASE_URL =
            "https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/"
    }

    init {
        loadAlbum()
    }

    fun loadAlbum() {
        viewModelScope.launch {
            try {
                val response = AlbumApi.retrofitService.getAlbum()
                if (!response.isSuccessful) {
                    throw Error(response.message())
                }
                val album = response.body() ?: throw Error(response.message())
                _data.value = album.copy(tracks = album.tracks.map { it.copy(album = album.title) })
            } catch (e: Exception) {
                throw e.fillInStackTrace()
            }
        }
    }

    fun playSong(song: Song) {
        mediaObserver.apply {
            if (player != null && player!!.isPlaying) {
                player?.stop()
                player?.reset()
                player?.setDataSource(
                    "${BASE_URL}${song.file}"
                )
            } else {
                player?.setDataSource(
                    "${BASE_URL}${song.file}"
                )
            }
        }.play()
        _data.value= _data.value?.run {copy (tracks = tracks.map{it.copy(playing = it.id == song.id)})}

    }

    fun pauseSong() {

        mediaObserver.apply {
            if (player != null) {
                player?.stop()
                player?.reset()
            }
        }
        _data.value= _data.value?.run {copy (tracks = tracks.map{it.copy(playing = false)}) }


    }

    fun pauseSong(song: Song) {
        if (song.playing) {
            mediaObserver.apply {
                if (player != null) {
                    player?.stop()
                    player?.reset()
                }
            }
            _data.value= _data.value?.run {copy (tracks = tracks.map{it.copy(playing = false)}) }
        }

    }
}