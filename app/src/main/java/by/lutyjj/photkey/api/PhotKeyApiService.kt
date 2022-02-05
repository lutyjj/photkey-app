package by.lutyjj.photkey.api

import by.lutyjj.photkey.models.Photo
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

interface PhotKeyApiService {
    @GET("photos")
    suspend fun getPhotos(): List<Photo>

    @GET("photos")
    suspend fun getPhotosByDate(@Query("date") date: String): List<Photo>

    @GET("photos")
    suspend fun getPhotosByLocation(@Query("location") location: String): List<Photo>

    @GET("photos/search")
    suspend fun existsByName(@Query("name") name: String): Boolean

    @Multipart
    @POST("photos")
    suspend fun postPhoto(@Part src: MultipartBody.Part)
}

object PhotKeyApi {
    fun initRetrofitService(url: String) {
        retrofitService = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(url)
            .build()
            .create(PhotKeyApiService::class.java)
    }

    lateinit var retrofitService: PhotKeyApiService
}
