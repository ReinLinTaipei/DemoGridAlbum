package com.demo.demogridphoto.presenter

import android.app.DownloadManager
import android.content.Context
import android.graphics.BitmapFactory
import com.demo.demogridphoto.activity.IDownloadView
import com.demo.demogridphoto.models.Photo
import com.demo.demogridphoto.models.DownloadState
import com.demo.demogridphoto.models.DownloadType

class ThumbnailPresenterImpl (
    private val downloadManager: DownloadManager,
    private val view: IDownloadView
) : IViewHolderPresenter {

    @Synchronized
    override fun checkDownloadState(context: Context, photo: Photo) {
        println("checkState: ${photo.result}")
        when (photo.result.state) {
            DownloadState.Init  -> {
                photo.isExist(context, DownloadType.Thumbnail) { exist, path ->
                    when (exist) {
                        true  -> {
                            println("$view, $path")
                            view.onResultBitmap(BitmapFactory.decodeFile(path))
                        }
                        false -> {
                            photo.addDownloadQueue(context, downloadManager, DownloadType.Thumbnail) { id ->
                                photo.result.downloadId = id
                                view.checkPendingQueue()
                            }
                        }
                    }
                }
            }
            DownloadState.Downloading -> {
                println("Not Completed")
                view.checkPendingQueue()
            }
            DownloadState.Completed -> view.onResultUri(photo.result.downloadUri)
        }
    }
}