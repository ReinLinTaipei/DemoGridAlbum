package com.demo.demogridphoto.presenter


interface IAdapterPresenter<T, R> {
    fun getItem(position: Int): T
    fun getCount(): Int
    fun setData(data: List<T>)
    fun updateItem(item: R, notifyChanged: (Int) -> Unit)
    fun checkPendingQueue()
    fun clearData(): Int
    fun stop()
}