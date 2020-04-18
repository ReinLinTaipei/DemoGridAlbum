package com.demo.demogridphoto.adapter

import android.app.DownloadManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.demogridphoto.R
import com.demo.demogridphoto.models.DownloadResult
import com.demo.demogridphoto.models.Photo
import com.demo.demogridphoto.presenter.GridAdapterPresenterImp
import com.demo.demogridphoto.presenter.IAdapterPresenter

class GridAdapter(
    private val downloadManager: DownloadManager,
    private val cellWidth: Int
) : RecyclerView.Adapter<PhotoViewHolder>() {

    private var listener: Listener? = null
    private val presenter: IAdapterPresenter<Photo, DownloadResult> by lazy {
        GridAdapterPresenterImp(downloadManager)
    }

    interface Listener {
        fun onClicked(photo: Photo)
        fun checkPendingQueue()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cell, parent, false)
        return PhotoViewHolder(view, cellWidth, downloadManager, listener)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(presenter.getItem(position))
    }

    override fun getItemCount(): Int = presenter.getCount()

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun setData(data: List<Photo>) {
        presenter.setData(data)
        notifyDataSetChanged()
    }

    fun clear() {
        notifyItemRangeRemoved(0, presenter.clearData())
    }

    fun updateImage(result: DownloadResult) {
        presenter.updateItem(result) { position ->
            notifyItemChanged(position)
        }
    }

    fun stopDownload() {
        presenter.stop()
    }

    @Synchronized
    fun checkPendingQueue() {
        presenter.checkPendingQueue()
    }
}


