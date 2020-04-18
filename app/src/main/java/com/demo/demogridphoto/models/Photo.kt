package com.demo.demogridphoto.models

import android.app.DownloadManager
import android.content.Context
import android.os.Parcelable
import com.demo.demogridphoto.findDownloadFile
import com.demo.demogridphoto.fileNamePNG
import com.demo.demogridphoto.web.WebAsync
import com.demo.demogridphoto.web.WebUtil
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Photo(
    @SerializedName("albumId")      val albumId: Int,
    @SerializedName("id" )          val id:      Int,
    @SerializedName("title")        val title:   String,
    @SerializedName("url")          val url:     String,
    @SerializedName("thumbnailUrl") val thumbnailUrl: String
): Parcelable {
    @IgnoredOnParcel lateinit var result: DownloadResult

    override fun toString(): String {
        return "Photo($albumId-$id): $title, $url, $thumbnailUrl"
    }

    override fun equals(other: Any?): Boolean {

        if (other == null) return false
        if (other !is Photo) return false

        return this.albumId == other.albumId && this.id == other.id
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    fun isExist(context: Context, downloadType: DownloadType, result: (Boolean, String) -> Unit) {
        val filename = when(downloadType) {
            DownloadType.Thumbnail -> this.thumbnailFilename
            DownloadType.Origin    -> this.originFilename
        }

        val file  = context.findDownloadFile(filename)
        val exist = file.exists()
        println("isPhotoExist: ($exist), ${file.path}")
        result(exist, file.path)
    }

    fun addDownloadQueue(context: Context, manager: DownloadManager, downloadType: DownloadType, resultAdded: (Long) -> Unit) {
        val url = when(downloadType) {
            DownloadType.Thumbnail -> this.thumbnailUrl
            DownloadType.Origin    -> this.url
        }
        WebAsync(object : WebAsync.Listener<Long> {
            override fun onLoading(url: String): Long? {
                val id = WebUtil.addDownloadQueue(context, manager, url, downloadType)
                println("add to download($id): (${result.position}), ${url.fileNamePNG(downloadType)}")
                return id
            }

            override fun onResult(result: Long?) {
                resultAdded(result ?: -1L)
            }
        }).execute(url)
    }

    private val thumbnailFilename: String
        get() = this.thumbnailUrl.fileNamePNG(DownloadType.Thumbnail)

    private val originFilename: String
        get() = this.url.fileNamePNG(DownloadType.Origin)
}
