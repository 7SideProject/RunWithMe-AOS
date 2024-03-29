package com.side.runwithme.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build


fun resizeBitmapFormUri(uri: Uri, context: Context): Bitmap? {
    val input = context.contentResolver.openInputStream(uri)

    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }

    var bitmap: Bitmap?
    BitmapFactory.Options().run {
        inSampleSize = calculateInSampleSize(options)
        bitmap = BitmapFactory.decodeStream(input, null, this)
    }

    bitmap = bitmap?.let {
        rotateImageIfRequired(context, bitmap!!, uri)
    }

    input?.close()

    return bitmap
}


fun calculateInSampleSize(options: BitmapFactory.Options): Int {
    var height = options.outHeight
    var width = options.outWidth

    var inSampleSize = 1

    while (height > MAX_HEIGHT || width > MAX_WIDTH) {
        height /= 2
        width /= 2
        inSampleSize *= 2
    }

    return inSampleSize
}

fun rotateImageIfRequired(context: Context, bitmap: Bitmap, uri: Uri): Bitmap? {
    val input = context.contentResolver.openInputStream(uri) ?: return null

    val exif = if (Build.VERSION.SDK_INT > 23) {
        ExifInterface(input)
    } else {
        ExifInterface(uri.path!!)
    }

    val orientation =
        exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
        else -> bitmap
    }
}

fun rotateImage(bitmap: Bitmap, degree: Int): Bitmap? {
    val matrix = Matrix()
    matrix.postRotate(degree.toFloat())
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

const val MAX_WIDTH = 1440 / 2
const val MAX_HEIGHT = 1050 / 2