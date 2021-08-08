package ru.geekbrains.android2.jpgtopng.presentation

import android.graphics.Bitmap
import android.net.Uri
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface MainView : MvpView {
    fun setToast(text: String)
    fun setImageBitmap(image: Bitmap)
    fun setTextPathPng(text: String)
    fun setTextPathJpg(text: String)
    fun setImageURI(imageUri: Uri)
    fun setImageRes(imageRes: Int)
}