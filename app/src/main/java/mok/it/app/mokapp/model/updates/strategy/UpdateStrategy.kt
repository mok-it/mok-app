package mok.it.app.mokapp.model.updates.strategy

import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.InstallStateUpdatedListener
import mok.it.app.mokapp.databinding.ActivityMainBinding

interface UpdateStrategy {
    fun shouldUpdate(info: AppUpdateInfo): Boolean
    val installStateUpdatedListener: InstallStateUpdatedListener
    fun startUpdate(info: AppUpdateInfo, binding: ActivityMainBinding)
    fun onResume() {
        //only needed in Immediate updates
    }

    fun onDestory() {
        //only needed in Flexible updates
    }
}