package ru.netology.album.dto

data class Song(
    val id: Int,
    val file: String,
    val album: String,
    var playing: Boolean = false
)

data class Album(
    val id: Int,
    val title: String,
    val subtitle: String,
    val artist: String,
    val published: String,
    val genre: String,
    val tracks: List<Song>
)
