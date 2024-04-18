package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottiePainter
import mok.it.app.mokapp.R
import mok.it.app.mokapp.compose.BadgeIcon
import mok.it.app.mokapp.compose.EditNumericValue
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.fragments.viewmodels.RewardsViewModel
import mok.it.app.mokapp.model.Reward

class RewardsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                RewardsScreen()
            }
        }

    @Composable
    private fun RewardsScreen() {
        val viewModel: RewardsViewModel by viewModels()
        val rewards = viewModel.rewards.collectAsState(initial = emptyList())

        Column {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        text = "Mancsaid száma",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                    BadgeIcon(
                        badgeNumber = userModel.points.toString(),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            LazyColumn {
                items(rewards.value) { reward ->
                    RewardItem(reward, ::canBeBoughtByCurrentUser, viewModel)
                }
            }
        }
    }

    enum class DialogType {
        REQUEST,
        DELETE,
        SAVE,
        NONE
    }

    @Composable
    fun RewardItem(
        reward: Reward,
        canBeBought: (Reward) -> Boolean = { true },
        viewModel: RewardsViewModel,
    ) {
        var cardOpen by remember { mutableStateOf(false) }

        var showDialog by remember { mutableStateOf(DialogType.NONE) }

        var editingQuantity by remember { mutableIntStateOf(reward.quantity) }
        var editingPrice by remember { mutableIntStateOf(reward.price) }

        val lottieComposition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.loading)
        )
        val lottiePainter = rememberLottiePainter(
            composition = lottieComposition,
            enableMergePaths = true
        )

        when (showDialog) {
            DialogType.DELETE -> {
                AlertDialog(
                    title = { Text("Jutalom törlése") },
                    onDismissRequest = { showDialog = DialogType.NONE },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.deleteReward(reward)
                            showDialog = DialogType.NONE
                        }) {
                            Text("Törlés")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showDialog = DialogType.NONE }) {
                            Text(stringResource(R.string.cancel))
                        }
                    },
                    text = {
                        Text("Biztosan törölni szeretnéd a jutalmat? Azoknak, akik már igényelték a jutalmat, nem fog ezzel eltűnni. ")
                    })
            }

            DialogType.SAVE -> {
                AlertDialog(
                    title = { Text("Változtatások mentése") },
                    onDismissRequest = { showDialog = DialogType.NONE },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.updateReward(
                                reward.copy(
                                    quantity = editingQuantity,
                                    price = editingPrice
                                )
                            )
                            cardOpen = false
                            showDialog = DialogType.NONE
                        }) {
                            Text("Mentés")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showDialog = DialogType.NONE }) {
                            Text(stringResource(R.string.cancel))
                        }
                    },
                    text = {
                        Text("Biztosan menteni szeretnéd a változtatásokat? Ha változtattál árat, akkor akik már igényelték a jutalmat, azoknak továbbra is a régi ár lesz érvényben. ")
                    })
            }

            DialogType.REQUEST -> {
                AlertDialog(
                    title = { Text("Biztosan igényelni szeretnéd a jutalmat?") },
                    text = { Text("${reward.price} mancs kerül majd levonásra tőled. Az igénylés nem visszamondható. ") },
                    onDismissRequest = { showDialog = DialogType.NONE },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.requestReward(reward)
                            showDialog = DialogType.NONE
                        }) {
                            Text(stringResource(R.string.ok))
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showDialog = DialogType.NONE }) {
                            Text("Mégsem")
                        }
                    }
                )
            }

            DialogType.NONE -> {}
        }
        Card(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            enabled = canBeBought(reward),
        )
        {
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.Start
            ) {
                AsyncImage(
                    model = reward.icon,
                    contentDescription = "Icon of the reward",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(50)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.no_image_icon),
                    placeholder = lottiePainter, //TODO does it move though? I don't think so
                    colorFilter = if (canBeBought(reward)) null else
                        ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                )
                Column(
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(1f),
                    horizontalAlignment = Alignment.Start,
                ) {
                    if (!cardOpen) {
                        OpenRewardCardContent(reward)
                    } else {
                        Text(
                            text = reward.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        EditNumericValue(
                            editingQuantity,
                            "Mennyiség",
                            onValueChange = { editingQuantity = it })
                        EditNumericValue(
                            editingPrice,
                            "Ár",
                            onValueChange = { editingPrice = it })

                        Row(
                            modifier = Modifier
                                .padding(top = 16.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                modifier = Modifier.padding(end = 8.dp),
                                onClick = {
                                    showDialog = DialogType.SAVE
                                }) {
                                Text("Mentés")
                            }
                            OutlinedButton(
                                onClick = {
                                    editingQuantity = reward.quantity
                                    editingPrice = reward.price
                                },
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(end = 12.dp, top = 12.dp)
                        .fillMaxHeight(),
                ) {
                    BadgeIcon(reward.price.toString(), isEnabled = canBeBought(reward))
                    if (canBeBought(reward)) {
                        IconButton(
                            onClick = { showDialog = DialogType.REQUEST },
                        )
                        {
                            Icon(
                                imageVector = Icons.Filled.AddCircle,
                                contentDescription = "Request reward",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    if (LocalInspectionMode.current || userModel.admin) { // for the sake of preview
                        IconButton(
                            onClick = {
                                showDialog = DialogType.DELETE
                                cardOpen = false
                            },
                        )
                        {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.request_reward_contentdescription),
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                        if (cardOpen) {
                            Spacer(
                                modifier = Modifier.weight(
                                    1f
                                )
                            )
                        }
                        IconButton(
                            onClick = { cardOpen = !cardOpen },
                            modifier = Modifier
                                .weight(1f, false)
                        )
                        {
                            Icon(
                                imageVector = if (cardOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Edit reward",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun OpenRewardCardContent(reward: Reward) {
        Text(
            text = reward.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.quantity, reward.quantity),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }

    private fun canBeBoughtByCurrentUser(reward: Reward) =
        userModel.points >= reward.price
                && reward.quantity > 0
                && !userModel.requestedRewards.contains(reward.documentId)
}