package com.demo.demogridphoto.activity

import android.graphics.Bitmap
import android.net.Uri

interface IDownloadView {
    fun onResultBitmap(bitmap: Bitmap?)
    fun onResultUri(uri: Uri?)
    fun checkPendingQueue()
}