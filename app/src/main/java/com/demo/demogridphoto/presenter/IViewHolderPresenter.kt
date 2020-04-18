package com.demo.demogridphoto.presenter

import android.content.Context
import com.demo.demogridphoto.models.Photo

interface IViewHolderPresenter {
    fun checkDownloadState(context: Context, photo: Photo)
}