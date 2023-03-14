package ru.netology.album.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.*
import okhttp3.ResponseBody
import ru.netology.album.api.AlbumApi
import ru.netology.album.dto.Album
import ru.netology.album.dto.Song
import ru.netology.album.ui.MediaLifecycleObserver

class AlbumViewModel(application: Application) : AndroidViewModel(application) {
    private val mediaObserver = MediaLifecycleObserver()
    companion object{
        private const val BASE_URL =
        "https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/"
    }

    val data: Flow<List<Song>>
    get() = loadAlbum().map { it.tracks }
    init {
            loadAlbum()
        }

    fun loadAlbum(): Flow<Album> = flow {
        try {
            val response = AlbumApi.retrofitService.getAlbum()
            if (!response.isSuccessful) {
                throw Error(response.message())
            }
            val album = response.body() ?: throw Error(response.message())
            emit(album)
        } catch (e: Exception) {
            throw e.fillInStackTrace()
        }
    }
    fun playSong(song: Song){
        mediaObserver.apply {
            player?.setDataSource(
                "${BASE_URL}${song.file}"
            )
        }.play()
    }
    fun pauseSong() {
        mediaObserver.player?.pause()
    }


}