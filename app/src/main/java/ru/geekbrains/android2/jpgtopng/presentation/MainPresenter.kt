package ru.geekbrains.android2.jpgtopng.presentation

import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import ru.geekbrains.android2.jpgtopng.data.Converter
import java.util.concurrent.TimeUnit

class MainPresenter(private val converter: Converter) : MvpPresenter<MainView>() {
    private var converterDisposable: CompositeDisposable? = null

    fun convertJpgToPng(imagePicked: Bitmap, pathImagePicked: String) {
        converterDisposable = CompositeDisposable()
        converterDisposable?.add(
            converter.convertJpgToPng(imagePicked, pathImagePicked)
                .delay(3, TimeUnit.SECONDS)
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.setToast("${it.first} converted to png.")
                    viewState.setImageBitmap(it.second)
                    viewState.setTextPathPng(it.first)
                }, {
                    viewState.setToast(it.message ?: "Error convert")
                })
        )
    }

    fun setImageURI(imageUri: Uri) {
        viewState.setImageURI(imageUri)
    }

    fun setTextPathPng(text: String) {
        viewState.setTextPathPng(text)
    }

    fun setTextPathJpg(text: String) {
        viewState.setTextPathJpg(text)
    }

    fun converterDispose() {
        converterDisposable?.dispose()

    }

    fun setImageRes(imageRes: Int) {
        viewState.setImageRes(imageRes)
    }

    override fun onDestroy() {
        converterDisposable?.dispose()
        super.onDestroy()
    }
}