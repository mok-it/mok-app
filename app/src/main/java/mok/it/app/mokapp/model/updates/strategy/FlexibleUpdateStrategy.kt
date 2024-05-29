package mok.it.app.mokapp.model.updates.strategy

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import mok.it.app.mokapp.databinding.ActivityMainBinding

private const val updateType = AppUpdateType.FLEXIBLE

class FlexibleUpdateStrategy(
        private val appUpdateManager: AppUpdateManager,
        private val context: AppCompatActivity
) : UpdateStrategy {
    override fun shouldUpdate(info: AppUpdateInfo): Boolean {
        val isUpdateAvailable =
                info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
        val isUpdateAllowed = info.isFlexibleUpdateAllowed
        return isUpdateAllowed && isUpdateAvailable
    }

    override val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        when (state.installStatus()) {
            InstallStatus.DOWNLOADED -> {
                Toast.makeText(
                        context,
                        "A frissítés befejezéséhez indítsd újra az appot.",
                        Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun startUpdate(info: AppUpdateInfo, binding: ActivityMainBinding) {
        context.setContentView(binding.root)
        val appUpdateOptions = AppUpdateOptions.newBuilder(updateType)
                .setAllowAssetPackDeletion(true)
                .setAppUpdateType(updateType)
                .build()
        appUpdateManager.startUpdateFlow(info, context, appUpdateOptions)
    }

    override fun onDestory() {
        appUpdateManager.unregisterListener(installStateUpdatedListener)
    }
}
