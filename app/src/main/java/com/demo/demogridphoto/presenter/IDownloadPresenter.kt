package com.demo.demogridphoto.presenter

import android.app.DownloadManager
import android.content.Context
import com.demo.demogridphoto.models.DownloadResult
import com.demo.demogridphoto.models.Photo

/**
 * view -> presenter
 */
interface IDownloadPresenter {
    fun startDownload(context: Context, photo: Photo)
    fun stopDownload()
    fun onDownloadCompleted(result: DownloadResult)
}