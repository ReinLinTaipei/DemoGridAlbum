package com.demo.demogridphoto.web

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.demo.demogridphoto.models.DownloadResult
import com.demo.demogridphoto.models.Photo
import com.demo.demogridphoto.fileNamePNG
import com.demo.demogridphoto.models.DownloadType
import com.demo.demogridphoto.parseJson
import com.demo.demogridphoto.saveBitmap
import java.io.File
import java.net.URL
import javax.net.ssl.HttpsURLConnection
object WebUtil {
    fun getPhotoList(url: String, response: (List<Photo>?) -> Unit) {
        WebAsync(object : WebAsync.Listener<List<Photo>> {
            override fun onLoading(url: String): List<Photo>? =
                buildConnection(url, "GET") { connection ->
                    connection.inputStream.bufferedReader().run {
                        val result = readText()
                        close()
                        result
                    }
                }?.parseJson<List<Photo>>()

            override fun onResult(result: List<Photo>?) {
                result?.mapIndexed { index, photo ->
                    photo.result = DownloadResult()
                    photo.result.position = index
                }
                response(result)
            }

        }).execute(url)
    }

    fun getPhoto(url: String, photoResult: (Bitmap?) -> Unit) {
        WebAsync(object : WebAsync.Listener<Bitmap> {
            override fun onLoading(url: String): Bitmap? =
                buildConnection(url, "POST") { connection ->
                    connection.doInput = true
                    connection.readTimeout = 10_000
                    val inputStream = connection.inputStream
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()
                    bitmap
                }

            override fun onResult(result: Bitmap?) {
                println("getPhoto: $result")
                result.let(photoResult)
            }
        }).execute(url)
    }

    fun addDownloadQueue(context: Context, downloadManager: DownloadManager, url: String, type: DownloadType): Long {
        val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), type.name)
        if (directory.exists().not())
            directory.mkdir()

        val downloadUri = Uri.parse(url)
        val request = DownloadManager.Request(downloadUri).setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
        ).setAllowedOverRoaming(false)
            .setTitle("${url.substringAfter("/")}.png")
            .setDescription("")
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, url.fileNamePNG(type))
        return downloadManager.enqueue(request)
    }

    private fun <T> buildConnection(
        url: String,
        method: String,
        response: (connection: HttpsURLConnection) -> T
    ): T? {
        val connection = URL(url).openConnection() as HttpsURLConnection
        connection.requestMethod = method
        return try {
            response(connection)
        } catch (e: Exception) {
            println("connection exception: $e")
            null
        } finally {
            connection.disconnect()
            println("connection disconnect")
        }
    }
}


