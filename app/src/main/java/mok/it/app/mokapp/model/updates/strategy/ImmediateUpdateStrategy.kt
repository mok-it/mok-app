package mok.it.app.mokapp.model.updates.strategy

import android.app.Activity.RESULT_OK
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import mok.it.app.mokapp.databinding.ActivityMainBinding
import mok.it.app.mokapp.ui.compose.theme.MokAppTheme
import mok.it.app.mokapp.utility.Utility.TAG

class ImmediateUpdateStrategy(
        private val appUpdateManager: AppUpdateManager,
        private val context: AppCompatActivity
) : UpdateStrategy {
    private val updateType = AppUpdateType.IMMEDIATE

    override val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        when (state.installStatus()) {
            InstallStatus.DOWNLOADED -> {
                Toast.makeText(
                        context,
                        "Új verzió letöltve. Telepítés...",
                        Toast.LENGTH_LONG
                ).show()
            }

            InstallStatus.INSTALLED -> {
                Toast.makeText(
                        context,
                        "Frissítés telepítve. Az alkalmazás újraindul.",
                        Toast.LENGTH_LONG
                ).show()
            }

            InstallStatus.CANCELED -> {
                Toast.makeText(
                        context,
                        "Frissítés megszakítva. Az alkalmazás használatához frissítés szükséges.",
                        Toast.LENGTH_LONG
                ).show()
                showMandatoryScreen()
            }
        }
    }

    override fun shouldUpdate(info: AppUpdateInfo): Boolean {
        val isUpdateAvailable =
                info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
        val isUpdateAllowed = info.isImmediateUpdateAllowed
        return isUpdateAllowed && isUpdateAvailable
    }

    override fun startUpdate(info: AppUpdateInfo, binding: ActivityMainBinding) {
        val appUpdateOptions = AppUpdateOptions.newBuilder(updateType)
                .setAllowAssetPackDeletion(true)
                .setAppUpdateType(updateType)
                .build()
        appUpdateManager.startUpdateFlow(info, context, appUpdateOptions)
                .addOnSuccessListener {
                    if (it == RESULT_OK) {
                        Log.i(TAG, "Az app frissítése sikeres.")
                        context.setContentView(binding.root)
                    } else {
                        if (it == 0) {
                            Log.w(TAG, "Update declined by user.")
                            Toast.makeText(
                                    context,
                                    "Az alkalmazás használatához frissítés szükséges.",
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                        showMandatoryScreen()
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, "app update failed")
                    Toast.makeText(
                            context,
                            "Frissítés sikertelen. Az alkalmazás használatához frissítés szükséges.",
                            Toast.LENGTH_SHORT
                    ).show()
                    showMandatoryScreen()
                }
    }


    override fun onResume() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                val appUpdateOptions = AppUpdateOptions.newBuilder(updateType)
                        .setAllowAssetPackDeletion(true)
                        .setAppUpdateType(updateType)
                        .build()
                appUpdateManager.startUpdateFlow(info, context, appUpdateOptions)
                        .addOnCanceledListener {
                            Log.w(TAG, "app update cancelled")
                            Toast.makeText(
                                    context,
                                    "Frissítés megszakítva. Az alkalmazás használatához frissítés szükséges.",
                                    Toast.LENGTH_SHORT
                            ).show()
                            showMandatoryScreen()
                        }
                        .addOnCompleteListener { Log.i(TAG, "app updated succesfully") }
                        .addOnFailureListener { Log.e(TAG, "app update failed") }
            }
        }
    }

    private fun showMandatoryScreen() {
        context.setContentView(ComposeView(context).apply {
            setContent {
                MokAppTheme { MandatoryUpdateScreen() }
            }
        })
    }
}

@Composable
fun MandatoryUpdateScreen() {
    Surface {
        Text(
                text = "Az alkalmazás használatához frissítés szükséges. Kérjük, frissítsd az alkalmazást a play áruházban.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(12.dp)
        )
    }
}