package com.demo.demogridphoto.presenter

import android.app.DownloadManager
import com.demo.demogridphoto.Constant
import com.demo.demogridphoto.models.DownloadResult
import com.demo.demogridphoto.models.DownloadState
import com.demo.demogridphoto.models.Photo
import com.demo.demogridphoto.web.WebAsync

class GridAdapterPresenterImp(private val downloadManager: DownloadManager) :
    IAdapterPresenter<Photo, DownloadResult> {

    private val data = arrayListOf<Photo>()

    override fun getItem(position: Int): Photo {
        return data[position]
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun setData(data: List<Photo>) {
        this.data.clear()
        this.data.addAll(data)
    }

    override fun updateItem(item: DownloadResult, notifyChanged: (Int) -> Unit) {
        this.data.firstOrNull { it.result == item }
            ?.let { photo ->
                photo.result.status = item.status
                when (item.isSuccess) {
                    true -> {
                        photo.result.downloadUri = item.downloadUri
                        notifyChanged(photo.result.position)
                    }
                    false -> println("download photo fail, $photo")
                }
            }
    }

    override fun checkPendingQueue() {
        WebAsync(object : WebAsync.Listener<Unit> {
            @Synchronized
            override fun onLoading(url: String): Unit? {
                data.filter { it.result.state == DownloadState.Downloading }
                    .map { it.result }
                    .also {
                        println("checkPendingQueue, size: ${it.size}")
                        if (it.count() > Constant.DownLoad_Queue_Limit) {
                            val removeList = it.subList(0, it.size - Constant.DownLoad_Queue_Limit)
                            cancelDownloading(removeList)
                        }
                    }
                return null
            }

            override fun onResult(result: Unit?) {
            }
        }).execute("")
    }

    override fun clearData(): Int {
        val size = getCount()
        data.clear()
        return size
    }

    override fun stop() {
        WebAsync(object : WebAsync.Listener<Unit> {
            @Synchronized
            override fun onLoading(url: String): Unit? {
                data.filter { it.result.state == DownloadState.Downloading }
                    .map { it.result }
                    .let { cancelDownloading(it) }
                return null
            }

            override fun onResult(result: Unit?) {
            }
        }).execute("")
    }

    private fun cancelDownloading(pendingList: List<DownloadResult>) {
        pendingList.map {
            println("resetPhoto: $it")
            downloadManager.remove(it.downloadId)
            it.reset()
        }
    }

}