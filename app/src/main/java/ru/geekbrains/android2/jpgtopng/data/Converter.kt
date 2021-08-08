package ru.geekbrains.android2.jpgtopng.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.reactivex.rxjava3.core.Single
import java.io.FileOutputStream

open class Converter {
    fun convertJpgToPng(bitmap: Bitmap, pathToBitmap: String): Single<Pair<String, Bitmap>> {
        val (pathImagePickedDir, nameImagePicked) = splitPathToBitmap(pathToBitmap)
        return Single.fromCallable {
            val pathImageOutput = "$pathImagePickedDir/$nameImagePicked.png"
            val imageOutputStream = FileOutputStream(pathImageOutput)

            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, imageOutputStream)) {
                return@fromCallable (pathImageOutput to BitmapFactory.decodeFile(pathImageOutput))
            } else {
                throw Exception("Converter failure")
            }
        }
    }

    private fun splitPathToBitmap(pathToBitmap: String): Pair<String, String> {
        val pathImagePickedParts = pathToBitmap.split("/")
        val pathImagePickedDir = pathImagePickedParts
            .subList(1, pathImagePickedParts.size - 1)
            .joinToString(prefix = "/", separator = "/")
        val nameImagePicked = pathImagePickedParts[pathImagePickedParts.size - 1]
            .split(".")[0]

        return pathImagePickedDir to nameImagePicked
    }


}