package com.demo.demogridphoto.adapter

import android.app.DownloadManager
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.demogridphoto.R
import com.demo.demogridphoto.activity.IDownloadView
import com.demo.demogridphoto.models.Photo
import com.demo.demogridphoto.presenter.IViewHolderPresenter
import com.demo.demogridphoto.presenter.ThumbnailPresenterImpl

class PhotoViewHolder(
    itemView: View,
    cellWidth: Int,
    downloadManager: DownloadManager,
    private val listener: GridAdapter.Listener?
) : RecyclerView.ViewHolder(itemView) {

    private val idText: TextView = itemView.findViewById(R.id.idTextView)
    private val titleText: TextView = itemView.findViewById(R.id.titleTextView)
    private val imageView: ImageView = itemView.findViewById(R.id.cellImageView)

    private val presenter: IViewHolderPresenter by lazy {
        ThumbnailPresenterImpl(downloadManager, object : IDownloadView {
            override fun onResultBitmap(bitmap: Bitmap?) {
                bitmap?.let { imageView.setImageBitmap(bitmap) }
            }

            override fun onResultUri(uri: Uri?) {
                imageView.setImageURI(uri)
            }

            override fun checkPendingQueue() {
                listener?.checkPendingQueue()
            }
        })
    }

    init {
        itemView.layoutParams.width  = cellWidth
        itemView.layoutParams.height = cellWidth
    }

    fun bind(photo: Photo) {
        photo.apply {
            idText.text = this.id.toString()
            titleText.text = this.title
            imageView.setImageDrawable(null)
            imageView.setOnClickListener { listener?.onClicked(photo) }

            presenter.checkDownloadState(itemView.context, photo)
        }
    }
}