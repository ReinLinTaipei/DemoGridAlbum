package com.demo.demogridphoto.models

import android.app.DownloadManager
import android.net.Uri
import com.demo.demogridphoto.statusMessage

data class DownloadResult(var downloadId: Long = -1,
                          var status: Int = 0,
                          var position: Int = -1,
                          var downloadUri: Uri? = null) {

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is DownloadResult) return false
        return this.downloadId == other.downloadId
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun toString(): String {
        return "(${this.position}) type:$state, $status, $message, \ndownloadId:$downloadId, uri: ${downloadUri?.path}"
    }

    val message: String
        get() =
            status.statusMessage()

    val isSuccess: Boolean
        get() = status == DownloadManager.STATUS_SUCCESSFUL

    val state: DownloadState
        get() = when {
            downloadId  == -1L  -> DownloadState.Init
            downloadUri == null -> DownloadState.Downloading
            downloadUri != null -> DownloadState.Completed
            else -> DownloadState.Init
        }

    @Synchronized
    fun reset() {
        status = 0
        downloadId = -1
        downloadUri = null
    }
}