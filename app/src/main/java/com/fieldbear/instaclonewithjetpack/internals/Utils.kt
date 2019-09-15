package com.fieldbear.instaclonewithjetpack.internals

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.inputmethod.InputMethodManager

import android.util.Patterns.*
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min
import kotlin.math.roundToInt
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.request.RequestOptions
import com.fieldbear.instaclonewithjetpack.R
import com.google.firebase.storage.FirebaseStorage


class Utils {
    companion object{
        fun displayTimeString(timeStamp:Long): String{

            val current = System.currentTimeMillis()

            val diff = (current - timeStamp)/1000
            val min = 60
            val hour = 3600
            val day = 86400

            return when (diff) {
                in 0..min -> "방금전" // "$diff 초전"
                in min..hour -> "${diff/min} 분전"
                in hour..day -> "${diff/hour} 시간전"
                in day..(day*10) -> "${diff/day} 일전"
                else -> SimpleDateFormat("yyyy.MM.dd").format(Date(timeStamp))
            }
        }

        fun isEmailValidate(email: String): Boolean{
            return EMAIL_ADDRESS.matcher(email).matches()
        }

        fun hideKeyboard(activity: Activity){
            val view = activity.currentFocus
            view?.let { v ->
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }

        fun hideKeyboard(view: View){
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun resizeImage(originImage:Bitmap, maxImageSize:Float): Bitmap{
            val ratio = min(maxImageSize / originImage.width, maxImageSize / originImage.height)
            val width = (ratio * originImage.width).roundToInt()
            val height = (ratio * originImage.height).roundToInt()

            val newBitmap = Bitmap.createScaledBitmap(originImage, width, height, false)
            return newBitmap
        }

        fun getResizedImage(context:Context, imageUri: Uri, imageSize:Int = 500): Uri  {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val originBmp = BitmapFactory.decodeStream(inputStream)
            val resizedBmp = resizeImage(
                originBmp,
                imageSize.toFloat()
            )

            inputStream?.close()

            val file = File(imageUri.path)
            val fileUri = Uri.fromFile(file)
            val outputStream = FileOutputStream(file)
            resizedBmp.compress(Bitmap.CompressFormat.JPEG, 98, outputStream)

            outputStream.flush()
            outputStream.close()

            return fileUri
        }

        fun showProfileImage(uid: String, imageView: ImageView){
            val ref =  FirebaseStorage.getInstance().reference
                .child("profileImage/${uid}.jpg")

            GlideApp.with(imageView)
                .load(ref)
                .circleCrop()
                .into(imageView)
        }
    }
}

typealias OnActivityResultType = (Int, Int, Intent?)->Unit

@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.append(StorageReference::class.java, InputStream::class.java, FirebaseImageLoader.Factory())
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)

        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        builder.setDefaultRequestOptions(
            RequestOptions().placeholder(circularProgressDrawable).error(R.mipmap.ic_launcher)
        )

    }
}

