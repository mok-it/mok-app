package mok.it.app.mokapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import mok.it.app.mokapp.R
import mok.it.app.mokapp.fragments.viewmodels.PhoneBookViewModel
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.utility.Utility.unaccent


class PhoneBookFragment : Fragment() {

    private val viewModel: PhoneBookViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                PhoneBookFragment()
            }
        }

    @Composable
    fun PhoneBookItem(
        user: User,
        onItemClick: (User) -> Unit,
        onCallClick: (User) -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onItemClick(user) },
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = user.photoURL,
                    contentDescription = "User's profile photo",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(8.dp)
                        .clip(CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        text = user.name, style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = user.phoneNumber.ifEmpty {
                            stringResource(id = R.string.no_phone_number)
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { onCallClick(user) }) {
                    Icon(Icons.Filled.Call, contentDescription = "Call")
                }
            }
        }
    }

    @SuppressLint("NotConstructor")
    @Composable
    @Preview
    fun PhoneBookFragment() {


        var searchQuery by remember { mutableStateOf("") }
        Column {
            val filteredUsers = viewModel.users.observeAsState().value
                ?.filter { user ->
                    user.name.unaccent().contains(searchQuery.trim().unaccent(), ignoreCase = true)
                            || user.phoneNumber.contains(searchQuery.trim())
                }.orEmpty().sortedBy { it.name }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Keresés") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Icon"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (filteredUsers.isEmpty()) {
                Text(
                    text = "Nincsenek a feltételeknek megfelelő emberek",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredUsers) { user ->
                        PhoneBookItem(
                            user = user,
                            onItemClick = {
                                findNavController().navigate(
                                    PhoneBookFragmentDirections.actionGlobalMemberFragment(
                                        user
                                    )
                                )
                            },
                            onCallClick = { _ ->
                                // if the device is capable of making phone calls, the button opens the dialer
                                if (isTelephonyEnabled() && user.phoneNumber.isNotEmpty()) {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:${user.phoneNumber}}")
                                    context?.let { it2 ->
                                        ContextCompat.startActivity(
                                            it2,
                                            intent,
                                            null
                                        )
                                    }
                                } else if (user.phoneNumber.isEmpty()) // if the user doesn't have a phone number, it shows a toast
                                    Toast.makeText(
                                        context,
                                        getString(R.string.no_phone_number),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                else // ...if not, it copies the number to the clipboard
                                {
                                    val clipboard =
                                        context?.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText(
                                        "label",
                                        user.phoneNumber
                                    )
                                    clipboard.setPrimaryClip(clip)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun isTelephonyEnabled(): Boolean {
        val telephonyManager = getSystemService(this.requireContext(), TelephonyManager::class.java)
        return (telephonyManager != null) && (telephonyManager.simState == TelephonyManager.SIM_STATE_READY)
    }
}