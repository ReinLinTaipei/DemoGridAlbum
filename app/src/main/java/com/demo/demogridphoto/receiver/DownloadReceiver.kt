package com.demo.demogridphoto.receiver

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.demo.demogridphoto.models.DownloadResult
import com.demo.demogridphoto.models.DownloadType

class DownloadReceiver(
    private val downloadManager: DownloadManager,
    private val listener: Listener?): BroadcastReceiver() {

    interface Listener  {
        fun getType(): DownloadType
        fun onResult(type: DownloadType, result: DownloadResult)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.action?.apply {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == this) {
                val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
                val query = DownloadManager.Query()
                query.setFilterById(downloadId)
                try {
                    val cursor = downloadManager.query(query)
                    cursor?.let {
                        if (cursor.moveToFirst()) {
                            val columnId = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                            val status = cursor.getInt(columnId)
                            val result = DownloadResult(downloadId = downloadId, status = status)
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                val uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                                result.downloadUri = Uri.parse(uri)
                            }
                            cursor.close()
                            println("Download receiver: $result")
                            listener?.onResult(listener.getType(), result)
                        }
                        else
                            cursor.close()
                    }
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}