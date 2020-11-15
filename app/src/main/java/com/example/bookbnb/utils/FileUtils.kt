package com.example.bookbnb.utils

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await


class FileUtils {
    companion object{
        fun getFileName(context: Context, uri: Uri): String? {
            var result: String? = null
            if (uri.getScheme().equals("content")) {
                val cursor: Cursor? =  context.contentResolver.query(uri, null, null, null, null)
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                } finally {
                    cursor?.close()
                }
            }
            if (result == null) {
                result = uri.getPath()
                val cut = result!!.lastIndexOf('/')
                if (cut != -1) {
                    result = result.substring(cut + 1)
                }
            }
            return result
        }

        suspend fun uploadImagesToFirebase(context: Context, imageUris: List<Uri>) : List<String>{
            val storage = Firebase.storage
            val imagesUrls: MutableList<String> = ArrayList()
            for (img: Uri in imageUris) {
                try {
                    val fileName: String? = getFileName(context, img)
                    val imgRef = storage.reference.child(fileName!!)
                    val downloadUrl = imgRef
                        .putFile(img)
                        .await()
                        .storage
                        .downloadUrl
                        .await()
                        .toString()
                    imagesUrls.add(downloadUrl)
                } catch (e: Exception) {
                    print(e.stackTrace)
                    throw(e)
                    //TODO: Handle error when uploading photo
                }
            }
            return imagesUrls
        }
    }
}