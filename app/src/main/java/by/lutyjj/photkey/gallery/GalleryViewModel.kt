package by.lutyjj.photkey.gallery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.lutyjj.photkey.api.PhotKeyApi
import by.lutyjj.photkey.models.Photo
import kotlinx.coroutines.launch

class GalleryViewModel : ViewModel() {
    private val _remotePhotos = MutableLiveData<List<Photo>>()

    val remotePhotos: LiveData<List<Photo>> = _remotePhotos

    init {
        getPhotos()
    }

    fun getPhotos() {
        viewModelScope.launch {
            try {
                _remotePhotos.value = PhotKeyApi.retrofitService.getPhotos()
            } catch (e: Exception) {
                Log.e("API error", e.toString())
            }
        }
    }

    fun getByDate(date: String) {
        viewModelScope.launch {
            try {
                _remotePhotos.value = PhotKeyApi.retrofitService.getPhotosByDate(date)
            } catch (e: Exception) {
                Log.e("API error", e.toString())
            }
        }
    }

    fun getByLocation(location: String) {
        viewModelScope.launch {
            try {
                _remotePhotos.value = PhotKeyApi.retrofitService.getPhotosByLocation(location)
            } catch (e: Exception) {
                Log.e("API error", e.toString())
            }
        }
    }
}
