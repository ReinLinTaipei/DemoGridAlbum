package com.demo.demogridphoto.activity

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.demo.demogridphoto.R
import com.demo.demogridphoto.models.DownloadResult
import com.demo.demogridphoto.models.DownloadType
import com.demo.demogridphoto.models.Photo
import com.demo.demogridphoto.presenter.IDownloadPresenter
import com.demo.demogridphoto.presenter.ProfilePresenterImpl
import com.demo.demogridphoto.receiver.DownloadReceiver
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : Activity(), IDownloadView {

    companion object {
        const val Bundle_Photo: String = "Bundle_Photo"
    }

    private val downloadManager: DownloadManager by lazy { getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager }
    private val presenter: IDownloadPresenter by lazy { ProfilePresenterImpl(downloadManager, this) }

    private val receiver: DownloadReceiver by lazy {
        DownloadReceiver(downloadManager, object : DownloadReceiver.Listener {
            override fun onResult(type: DownloadType, result: DownloadResult) {
                if (type == getType())
                    presenter.onDownloadCompleted(result)
            }

            override fun getType(): DownloadType = DownloadType.Origin
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profileImageView.setImageResource(R.drawable.ic_sync_black_24dp)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        intent?.getParcelableExtra<Photo>(Bundle_Photo)?.let { photo ->
            idTextView.text    = photo.id.toString()
            titleTextView.text = photo.title

            showLoading(true)
            presenter.startDownload(this, photo)
        }
    }

    override fun onResultBitmap(bitmap: Bitmap?) {
        bitmap?.let { profileImageView.setImageBitmap(bitmap) }
        showLoading(false)
    }

    override fun onResultUri(uri: Uri?) {
        uri?.let {
            profileImageView.setImageURI(it)
        }
        showLoading(false)
    }

    override fun checkPendingQueue() {
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        presenter.stopDownload()
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
