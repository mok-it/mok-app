package mok.it.app.mokapp.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.phone_contact_item.view.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.baseclasses.BaseFireFragment
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.PhoneBookViewHolder
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager


class PhoneBookFragment : BaseFireFragment() {
    private lateinit var recyclerView: RecyclerView
    lateinit var adapter: FirestoreRecyclerAdapter<*, *>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_phone_list, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.stopListening()
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeAdapter()

        recyclerView = this.requireView().findViewById(R.id.fragment_phone_list)
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            WrapContentLinearLayoutManager(this.context)
    }

    private fun isTelephonyEnabled(): Boolean {
        val telephonyManager = getSystemService(this.requireContext(), TelephonyManager::class.java)
        return (telephonyManager != null) && (telephonyManager.simState == TelephonyManager.SIM_STATE_READY)
    }

    private fun initializeAdapter() {
        val options: FirestoreRecyclerOptions<User?> = FirestoreRecyclerOptions.Builder<User>()
            .setQuery(firestore.collection(userCollectionPath).orderBy("name"), User::class.java)
            .build()

        adapter =
            object : FirestoreRecyclerAdapter<User?, PhoneBookViewHolder?>(options) {
                var context: Context? = null
                override fun onBindViewHolder(
                    holder: PhoneBookViewHolder,
                    position: Int,
                    model: User
                ) {
                    val ivImg: ImageView = holder.itemView.findViewById(R.id.contact_image)
                    loadImage(ivImg, model.photoURL)
                    holder.itemView.contact_name.text = model.name
                    holder.itemView.phone_number.text = model.email
                    //TODO change to phone number

                    holder.itemView.call_button.setOnClickListener {
                        // if the device is capable of making phone calls, the button opens the dialer
                        if (isTelephonyEnabled()) {
                            val intent = Intent(Intent.ACTION_DIAL)
                            //TODO change to their real number
                            intent.data = Uri.parse("tel:0123456789")
                            this.context?.let { it2 ->
                                ContextCompat.startActivity(
                                    it2,
                                    intent,
                                    null
                                )
                            }
                        } else // ...if not, it copies the number to the clipboard
                        {
                            //TODO change to their real number
                            val clipboard =
                                this.context?.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText(
                                "label",
                                "0123456789"
                            )
                            clipboard.setPrimaryClip(clip)
                        }
                    }

                    /* holder.itemView.sms_button.setOnClickListener {
                         val intent = Intent(Intent.ACTION_SENDTO)
                         intent.data = Uri.parse("smsto:0123456789")
                         this.context?.let { it2 -> ContextCompat.startActivity(it2, intent, null) }
                     }*/
                }

                override fun onCreateViewHolder(group: ViewGroup, i: Int): PhoneBookViewHolder {
                    val view: View = LayoutInflater.from(group.context)
                        .inflate(R.layout.phone_contact_item, group, false)
                    return PhoneBookViewHolder(view)
                }

                override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
                    super.onAttachedToRecyclerView(recyclerView)
                    context = recyclerView.context
                }

                fun loadImage(imageView: ImageView, imageURL: String): Boolean {
                    return try {
                        Picasso.get().apply {
                            load(imageURL).into(imageView)
                        }
                        true
                    } catch (e: Exception) {
                        false
                    }
                }
            }
    }
}