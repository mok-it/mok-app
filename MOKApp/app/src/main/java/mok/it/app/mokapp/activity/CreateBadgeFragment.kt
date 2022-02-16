package mok.it.app.mokapp.activity

import android.app.AlertDialog
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import mok.it.app.mokapp.R


/**
 * TODO description
 */

class CreateBadgeFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_badge, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*val builder = AlertDialog.Builder(context)

        val displayRectangle = Rect(0, 0, 200, 100)

        view.minimumWidth = (displayRectangle.width() * 1f).toInt()
        view.minimumHeight = (displayRectangle.height() * 1f).toInt()*/


        /*builder.setView(view)
        val alertDialog = builder.create()
        val buttonOk: Button = view.findViewById(R.id.button2)
        buttonOk.setOnClickListener(View.OnClickListener { alertDialog.dismiss() })
        alertDialog.show()*/
        
    }

    companion object {

        val TAG = "CreateBadgeFragment"

        fun newInstance() =
            CreateBadgeFragment()

        /*fun createDialog(view: View, fragment: Fragment) {
            val displayRectangle = Rect()
            fragment.activity?.window?.let { window ->
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle)
                val builder = AlertDialog.Builder(fragment.context)
                val viewGroup: ViewGroup = fragment.view.findViewById()
                val dialogView: View =
                    LayoutInflater.from(view.getContext())
                        .inflate(R.layout.fragment_create_badge, viewGroup, false)
                dialogView.minimumWidth = (displayRectangle.width() * 1f) as Int
                dialogView.minimumHeight = (displayRectangle.height() * 1f) as Int
                builder.setView(dialogView)
                val alertDialog = builder.create()
                val buttonOk: Button = dialogView.findViewById(R.id.button2)
                buttonOk.setOnClickListener(View.OnClickListener { alertDialog.dismiss() })
                alertDialog.show()
            } ?: run {

                Log.e(TAG, "Could not create dialog")

            }

        }*/

    }
}