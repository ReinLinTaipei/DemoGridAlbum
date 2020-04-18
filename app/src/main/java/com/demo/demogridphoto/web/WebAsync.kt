package com.demo.demogridphoto.web

import android.os.AsyncTask

class WebAsync<T>(private val mListener: Listener<T>?) : AsyncTask<String, Void, T?>() {

    interface Listener<T> {
        fun onLoading(url: String): T?
        fun onResult(result: T?)
    }

    override fun doInBackground(vararg params: String?): T? {

        println("doInBackground: ${params[0]}")

        return try {
            when(val url = params[0]) {
                null -> null
                else -> mListener?.onLoading(url)
            }
        } catch (e: Exception) {
            println("doInBackground Exception: $e")
            null
        }
    }

    override fun onPostExecute(result: T?) {
        mListener?.onResult(result)
    }
}