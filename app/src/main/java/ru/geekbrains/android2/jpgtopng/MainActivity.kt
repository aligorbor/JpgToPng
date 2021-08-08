package ru.geekbrains.android2.jpgtopng

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import ru.geekbrains.android2.jpgtopng.data.Converter
import ru.geekbrains.android2.jpgtopng.databinding.ActivityMainBinding
import ru.geekbrains.android2.jpgtopng.presentation.MainPresenter
import ru.geekbrains.android2.jpgtopng.presentation.MainView

const val REQUEST_WRITE_STORAGE_REQUEST_CODE = 111
const val REQUEST_GET_CONTENT = 222

class MainActivity : MvpAppCompatActivity(), MainView {
    private lateinit var vb: ActivityMainBinding
    private val presenter by moxyPresenter { MainPresenter(Converter()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityMainBinding.inflate(layoutInflater)
        setContentView(vb.root)
        requestPermissions()
        vb.buttonLoadImage.setOnClickListener {
            pickImage()
        }
        vb.buttonConvertImage.setOnClickListener {
            if (vb.textPathJpg.text.toString() != "") {
                presenter.convertJpgToPng(
                    (vb.imageConverted.drawable as BitmapDrawable).bitmap,
                    vb.textPathJpg.text.toString()
                )
                presenter.setTextPathPng(getString(R.string.conv_in_progr))
            }
        }
        vb.buttonCancelConvert.setOnClickListener {
            if (vb.textPathJpg.text.toString() != "") {
                presenter.converterDispose()
                presenter.setTextPathPng(getString(R.string.conv_cancel))
                presenter.setTextPathJpg("")
                presenter.setImageRes(R.drawable.ic_baseline_add_a_photo_24)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        vb.buttonLoadImage.isEnabled =
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK &&
            requestCode == REQUEST_GET_CONTENT &&
            data != null
        ) {
            data.data?.let {
                presenter.setImageURI(it)
                presenter.setTextPathJpg(getPathFromUri(it) ?: "")
                presenter.setTextPathPng("")
            }
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            REQUEST_WRITE_STORAGE_REQUEST_CODE
        )
    }

    private fun hasPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpg"))
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            REQUEST_GET_CONTENT
        )
    }

    private fun getPathFromUri(contentUri: Uri): String? {
        var res: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contentUri, projection, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(projection[0])
            columnIndex.let {
                res = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        return res
    }

    override fun setToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    override fun setImageBitmap(image: Bitmap) {
        vb.imageConverted.background = null
        vb.imageConverted.setImageBitmap(image)
    }

    override fun setTextPathPng(text: String) {
        vb.textPathPng.text = text
    }

    override fun setTextPathJpg(text: String) {
        vb.textPathJpg.text = text
    }

    override fun setImageURI(imageUri: Uri) {
        vb.imageConverted.background = null
        vb.imageConverted.setImageURI(imageUri)
    }

    override fun setImageRes(imageRes: Int) {
        vb.imageConverted.background = null
        vb.imageConverted.setImageResource(imageRes)
    }
}