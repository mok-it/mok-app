package mok.it.app.mokapp.utility

import android.content.res.Resources
import android.graphics.Rect
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import java.text.Normalizer

/**
 * Utility class for various functions
 */
object Utility {
    fun CharSequence.unaccent(): String {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return "\\p{InCombiningDiacriticalMarks}+".toRegex().replace(temp, "")
    }

    fun getIconFileName(iconURL: String): String {
        val re = Regex("[^A-Za-z\\d ]")
        val alphanum = re.replace(iconURL, "")

        return "$alphanum.png"
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
}