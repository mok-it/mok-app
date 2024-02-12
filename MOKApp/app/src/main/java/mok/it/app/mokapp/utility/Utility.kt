package mok.it.app.mokapp.utility

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import mok.it.app.mokapp.R
import java.text.Normalizer

/**
 * Utility class for various functions
 */
object Utility {

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
     * Call this method (in onViewCreated or later) to set
     * the width of the dialog to a percentage of the current
     * screen width.
     */
    fun DialogFragment.setWidthPercent(percentage: Int) {
        val percent = percentage.toFloat() / 100
        val dm = Resources.getSystem().displayMetrics
        val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
        val percentWidth = rect.width() * percent
        dialog?.window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    /**
     * Call this method (in onViewCreated or later)
     * to make the dialog near-full screen.
     */
    fun DialogFragment.setFullScreen() {
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
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
                    .placeholder(circularProgressDrawable)  //TODO doesn't work with glide, doesn't work with picasso either, anyone any ideas?
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