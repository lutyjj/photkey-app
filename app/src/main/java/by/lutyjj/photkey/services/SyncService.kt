package by.lutyjj.photkey.services

import android.app.Service
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Intent
import android.os.Bundle
import android.os.FileUtils.copy
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import by.lutyjj.photkey.api.PhotKeyApi
import by.lutyjj.photkey.models.LocalPhoto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

class SyncService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val numberOfPhotos = preferences.getInt("number_of_photos", 10)

        val localPhotos = fetchLocalPhotos(numberOfPhotos)
        showToast(localPhotos.size)
        syncPhotos(localPhotos)

        return START_STICKY
    }

    private fun showToast(listSize: Int) {
        Toast.makeText(
            applicationContext, "Syncing $listSize photos",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun fetchLocalPhotos(limit: Int): List<LocalPhoto> {
        val images = mutableListOf<LocalPhoto>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.RELATIVE_PATH
        )
        val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("DCIM%")
        val sortOrder =
            "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            Bundle().apply {
                putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                putString(
                    ContentResolver.QUERY_ARG_SQL_SORT_ORDER,
                    sortOrder
                )
                putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
                putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs)
            }, null
        )
            ?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    val image = LocalPhoto(displayName, contentUri)

                    images.add(image)
                }
            }

        return images
    }

    private fun syncPhotos(photos: List<LocalPhoto>) {
        photos.forEach { photo ->
            CoroutineScope(Dispatchers.Main).launch {
                postPhoto(photo)
            }
        }
    }

    private suspend fun postPhoto(localPhoto: LocalPhoto) = withContext(Dispatchers.IO) {
        try {
            val doesExist = PhotKeyApi.retrofitService.existsByName(localPhoto.name)
            if (!doesExist) {
                val inputStream = contentResolver.openInputStream(localPhoto.srcUri)
                val tempFile = File.createTempFile("temp", localPhoto.name)
                val outputStream = FileOutputStream(tempFile)
                inputStream?.let {
                    copy(inputStream, outputStream)
                }
                outputStream.flush()
                val filePart = constructMultiPartFile(localPhoto, tempFile)

                val response = PhotKeyApi.retrofitService.postPhoto(filePart)
                Log.d("PhotKey Response", response.toString())
            } else {
                Log.d("PhotKey Response", "${localPhoto.name} already exists.")
            }
        } catch (e: Exception) {
            Log.e("PhotKey Response", e.toString())
        }
    }

    private fun constructMultiPartFile(
        localPhoto: LocalPhoto,
        tempFile: File
    ): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            "src", localPhoto.name, RequestBody.create(
                MediaType.parse("image/*"), tempFile
            )
        )
    }
}