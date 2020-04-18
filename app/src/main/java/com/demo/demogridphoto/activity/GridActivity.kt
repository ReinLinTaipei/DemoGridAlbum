package com.demo.demogridphoto.activity

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.demo.demogridphoto.Constant
import com.demo.demogridphoto.receiver.DownloadReceiver
import com.demo.demogridphoto.R
import com.demo.demogridphoto.adapter.GridAdapter
import com.demo.demogridphoto.web.WebUtil
import com.demo.demogridphoto.models.DownloadResult
import com.demo.demogridphoto.models.DownloadType
import com.demo.demogridphoto.models.Photo
import kotlinx.android.synthetic.main.activity_grid.*
import kotlinx.android.synthetic.main.activity_grid.progressBar
import kotlinx.android.synthetic.main.activity_profile.*

class GridActivity : Activity() {

    companion object {
        const val RequestCode_Profile: Int = 1
    }

    private val downLoadManager: DownloadManager by lazy { getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager }

    private val gridAdapter: GridAdapter by lazy {
        GridAdapter(downLoadManager, (resources.displayMetrics.widthPixels - 5) / 4)
    }

    private val receiver: DownloadReceiver by lazy {
        DownloadReceiver(downLoadManager, object : DownloadReceiver.Listener {
            override fun onResult(type: DownloadType, result: DownloadResult) {
                if (type == getType())
                    gridAdapter.updateImage(result)
            }

            override fun getType(): DownloadType = DownloadType.Thumbnail
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid)
        println("onCreate $savedInstanceState")

        gridAdapter.setListener(object : GridAdapter.Listener {
            override fun onClicked(photo: Photo) {
                stopDownload()
                goToProfilePage(photo)
            }

            override fun checkPendingQueue() {
                gridAdapter.checkPendingQueue()
            }
        })

        recyclerView.apply {
            layoutManager = GridLayoutManager(this@GridActivity, 4)
            adapter = gridAdapter
        }

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        fetchPhotoListInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        println("onActivityResult $requestCode")
        when (requestCode) {
            RequestCode_Profile -> registerReceiver(
                receiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onResume() {
        super.onResume()
        println("onResume")
        gridAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        println("onDestroy")
        stopDownload()
    }

    private fun fetchPhotoListInfo() {
        gridAdapter.clear()
        showLoading(true)

        WebUtil.getPhotoList(Constant.Photo_URL) { photoList ->
            showLoading(false)
            gridAdapter.clear()
            photoList?.let {
                gridAdapter.setData(it)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun goToProfilePage(photo: Photo) {
        val intent = Intent(this@GridActivity, ProfileActivity::class.java)
        val bundle = Bundle();
        bundle.putParcelable(ProfileActivity.Bundle_Photo, photo)
        intent.putExtras(bundle)
        startActivityForResult(intent, RequestCode_Profile)
    }

    private fun stopDownload() {
        unregisterReceiver(receiver)
        gridAdapter.stopDownload()
    }
}