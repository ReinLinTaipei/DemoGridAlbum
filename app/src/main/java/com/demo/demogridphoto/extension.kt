package com.demo.demogridphoto

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.ContentUris
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.demo.demogridphoto.models.DownloadType
import com.demo.demogridphoto.web.WebUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.net.URI

inline fun <reified T> String.parseJson(): T {
    val typeToken = object : TypeToken<T>() {}.type
    return Gson().fromJson(this, typeToken)
}

fun Bitmap.saveBitmap(context: Context, name: String) {
    val file = File(context.filesDir, name)
    val fileOutputStream = FileOutputStream(file)
    println("saveBitmap: ${file.path}")

    this.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
    fileOutputStream.flush()
    fileOutputStream.close()
    this.recycle()
}

fun Int.statusMessage(): String =
    when (this) {
        DownloadManager.STATUS_FAILED -> "Download has been failed, please try again"
        DownloadManager.STATUS_PAUSED -> "Paused"
        DownloadManager.STATUS_PENDING -> "Pending"
        DownloadManager.STATUS_RUNNING -> "Downloading..."
        DownloadManager.STATUS_SUCCESSFUL -> "Image downloaded successfully"
        else -> "There's noting to download, code($this)"
    }

fun Activity.askPermission(requestCode: Int, result: () -> Unit) {
    if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            AlertDialog.Builder(this)
                .setTitle("Permission required")
                .setMessage("Permission required to save photos from the web.")
                .setPositiveButton("Accept") { dialog, _ ->
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        requestCode
                    )
                    dialog.dismiss()
                }
                .setNegativeButton("Deny") { dialog, _ -> dialog.cancel() }
                .show()
        } else
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                requestCode
            )
    } else
        result()
}


fun Context.findDownloadFile(filename: String): File {
    val directory = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    return File(directory, filename)
}

fun String.fileNamePNG(downloadType: DownloadType): String =
    "${downloadType.name}/${this.substringAfterLast("/")}.png"



