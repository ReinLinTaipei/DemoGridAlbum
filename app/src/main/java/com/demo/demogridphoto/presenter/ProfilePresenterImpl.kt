package com.demo.demogridphoto.presenter

import android.app.DownloadManager
import android.content.Context
import android.graphics.BitmapFactory
import com.demo.demogridphoto.activity.IDownloadView
import com.demo.demogridphoto.models.DownloadResult
import com.demo.demogridphoto.models.DownloadState
import com.demo.demogridphoto.models.DownloadType
import com.demo.demogridphoto.models.Photo

class ProfilePresenterImpl (private val downloadManager: DownloadManager,
                            private val view: IDownloadView): IDownloadPresenter {

    private val selfResult: DownloadResult by lazy { DownloadResult() }

    override fun startDownload(context: Context, photo: Photo) {
        photo.isExist(context, DownloadType.Origin) { exist, path ->
            when (exist) {
                true  -> view.onResultBitmap(BitmapFactory.decodeFile(path))
                false -> addToDownloadQueue(context, photo)
            }
        }
    }

    override fun onDownloadCompleted(result: DownloadResult) {
        println("onDownloadCompleted: ${selfResult.downloadId}/$result")
        if (selfResult != result)
            return

        selfResult.downloadUri = result.downloadUri
        if (result.isSuccess)
            view.onResultUri(result.downloadUri)
    }

    override fun stopDownload() {
        selfResult.let {
            println("stop download: $it")
            if (it.state != DownloadState.Completed)
                downloadManager.remove(it.downloadId)
        }
    }

    private fun addToDownloadQueue(context: Context, photo: Photo) {
        photo.result = selfResult
        photo.addDownloadQueue(context, downloadManager, DownloadType.Origin) {
            selfResult.downloadId = it
            println("addToDownloadQueue: $selfResult")
            view.checkPendingQueue()
        }
    }
}