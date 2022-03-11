package com.khudyakovvladimir.vhcloudnotepad.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import android.widget.ImageView
import java.io.*
import javax.inject.Inject


class ImageHelper @Inject constructor(){

    companion object {
        val JPEG = Bitmap.CompressFormat.JPEG
        val PNG = Bitmap.CompressFormat.PNG
    }

     fun saveToInternalStorage(name: String, bitmapImage: Bitmap, appFolder: File, mimeType: Bitmap.CompressFormat): String? {
         val imageName = "$name.jpg"
         val myPath = File(appFolder, imageName)

         var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(myPath)
            bitmapImage.compress(mimeType, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return appFolder.absolutePath
    }


     fun loadImageFromStorage(path: String, view: ImageView, viewId: Int) {
        try {
            val bitmap = BitmapFactory.decodeStream(FileInputStream(path))
            val img: ImageView = view.findViewById(viewId) as ImageView
            img.setImageBitmap(setExifDataToBitmap(path, bitmap))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    fun saveEmptyFile(name: String, appFolder: File): String? {
        val imageName = "$name.jpg"
        val myPath = File(appFolder, imageName)

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(myPath)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return appFolder.absolutePath
    }

    fun getFile(name: String, appFolder: File): File {
        val imageName = "$name.jpg"
        val file = File(appFolder, imageName)
        Log.d("TAG", "getFile() - file = $file")

        return file
    }

    private fun setExifDataToBitmap(fileName: String, bitmap: Bitmap): Bitmap? {
        val exifInterface = ExifInterface(fileName)
        val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

        var rotatedBitmap: Bitmap? = null
        when(orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> { rotatedBitmap = rotateImage(bitmap, 90F) }
            ExifInterface.ORIENTATION_ROTATE_180 -> { rotatedBitmap = rotateImage(bitmap, 180F) }
            ExifInterface.ORIENTATION_ROTATE_270 -> { rotatedBitmap = rotateImage(bitmap, 277F) }
            ExifInterface.ORIENTATION_NORMAL-> { rotatedBitmap = bitmap }
        }
        return  rotatedBitmap
    }

    private fun rotateImage(bitmap: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}