package ru.netology.album.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import ru.netology.album.dto.Album
import ru.netology.album.dto.Song

interface ApiService {

    @GET("album.json")
    suspend fun getAlbum(): Response<Album>

    }
    object AlbumApi {
        private const val BASE_URL =
            "https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/"

        /* private val logging = HttpLoggingInterceptor().apply{
            if (BuildConfig.DEBUG){
                level = HttpLoggingInterceptor.Level.BODY
            }
        }*/

        /*private val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()*/

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            //.client(client)
            .baseUrl(BASE_URL)
            .build()

        val retrofitService by lazy {
            retrofit.create<ApiService>()
        }

}
