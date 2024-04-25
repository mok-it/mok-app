package mok.it.app.mokapp.feature.phonebook_list

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import mok.it.app.mokapp.R
import mok.it.app.mokapp.databinding.CardPhonebookItemBinding
import mok.it.app.mokapp.databinding.FragmentPhoneListBinding
import mok.it.app.mokapp.model.Collections
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.ui.recyclerview.PhoneBookViewHolder
import mok.it.app.mokapp.ui.recyclerview.WrapContentLinearLayoutManager
import mok.it.app.mokapp.utility.Utility


class PhoneBookFragment : Fragment() {
    private lateinit var binding: FragmentPhoneListBinding

    lateinit var adapter: FirestoreRecyclerAdapter<*, *>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeAdapter()

        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager =
            WrapContentLinearLayoutManager(this.context)
    }

    private fun isTelephonyEnabled(): Boolean {
        val telephonyManager = getSystemService(this.requireContext(), TelephonyManager::class.java)
        return (telephonyManager != null) && (telephonyManager.simState == TelephonyManager.SIM_STATE_READY)
    }

    private fun initializeAdapter() {
        val options: FirestoreRecyclerOptions<User?> = FirestoreRecyclerOptions.Builder<User>()
            .setQuery(
                Firebase.firestore.collection(Collections.USERS).orderBy("name"),
                User::class.java
            )
            .build()

        adapter =
            object : FirestoreRecyclerAdapter<User?, PhoneBookViewHolder?>(options) {
                lateinit var context: Context

                override fun onCreateViewHolder(parent: ViewGroup, i: Int) = PhoneBookViewHolder(
                    CardPhonebookItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )

                override fun onBindViewHolder(
                    holder: PhoneBookViewHolder,
                    position: Int,
                    model: User
                ) {
                    val ivImg: ImageView = holder.binding.contactImage
                    Utility.loadImage(ivImg, model.photoURL, context)
                    holder.binding.contactName.text = model.name
                    holder.binding.phoneNumber.text =
                        model.phoneNumber.ifEmpty { getString(R.string.no_phone_number) }

                    holder.binding.contactItem.setOnClickListener {
                        findNavController().navigate(
                            PhoneBookFragmentDirections.actionGlobalMemberFragment(
                                model
                            )
                        )
                    }

                    holder.binding.callButton.setOnClickListener {
                        // if the device is capable of making phone calls, the button opens the dialer
                        if (isTelephonyEnabled() && model.phoneNumber.isNotEmpty()) {
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:${model.phoneNumber}}")
                            this.context.let { it2 ->
                                ContextCompat.startActivity(
                                    it2,
                                    intent,
                                    null
                                )
                            }
                        } else if (model.phoneNumber.isEmpty()) // if the user doesn't have a phone number, it shows a toast
                            Toast.makeText(
                                context,
                                getString(R.string.no_phone_number),
                                Toast.LENGTH_SHORT
                            ).show()
                        else // ...if not, it copies the number to the clipboard
                        {
                            val clipboard =
                                this.context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText(
                                "label",
                                model.phoneNumber
                            )
                            clipboard.setPrimaryClip(clip)
                        }
                    }
                }

                override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
                    super.onAttachedToRecyclerView(recyclerView)
                    context = recyclerView.context
                }
            }
    }
}