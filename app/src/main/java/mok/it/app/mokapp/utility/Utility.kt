package mok.it.app.mokapp.utility

import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import mok.it.app.mokapp.R
import java.text.Normalizer

/**
 * Utility class for various functions
 */
object Utility {
    val Any.TAG: String
        get() {
            val tag = javaClass.simpleName
            return if (tag.length <= 23) tag else tag.substring(0, 23)
        }

    fun CharSequence.unaccent(): String {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return "\\p{InCombiningDiacriticalMarks}+".toRegex().replace(temp, "")
    }

    /**
     * Create an icon file name from the URL by removing all non-alphanumeric characters
     * @param iconURL the URL of the icon
     * @return the file name of the icon
     */
    fun getIconFileName(iconURL: String): String {
        return iconURL.filter { it.isLetterOrDigit() || it.isWhitespace() } + ".png"
    }

    /**
     * Tries to load the image provided into the given view. If that did not
     * succeed, it tries to load the default 'broken' image. If that also
     * fails, leaves the image empty and logs an error message.
     */
    fun <T> loadImage(
            imageView: ImageView,
            imageURLOrDrawable: T?,
            context: Context,
            callback: Callback? = null
    ) {
        if (tryLoadingImage(imageView, imageURLOrDrawable, context, callback)) return
        if (tryLoadingImage(
                        imageView,
                        R.drawable.no_image_icon,
                        context,
                        callback
                )
        ) return
    }

    /**
     * Tries to load an image into the given image view. If for some reason
     * the provided URL does not point to a valid image file, false is returned.
     *
     * @return true if the function succeeded, false if failed
     */
    private fun <T> tryLoadingImage(
            imageView: ImageView,
            imageURLOrDrawable: T?,
            context: Context,
            callback: Callback? = null
    ): Boolean {
        val circularProgressDrawable = CircularProgressDrawable(context).apply {
            strokeWidth = 5f
            centerRadius = 30f
            start()
        }

        imageView.setImageDrawable(circularProgressDrawable)
        return try {
            Picasso.get().apply {
                when (imageURLOrDrawable) {
                    is String -> load(imageURLOrDrawable)
                    is Int -> load(imageURLOrDrawable)
                    else -> throw IllegalArgumentException("Unsupported type of imageURLOrDrawable")
                }
                        .placeholder(circularProgressDrawable)
                        .apply {
                            if (callback != null) this.into(imageView, callback) else this.into(
                                    imageView
                            )
                        }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}