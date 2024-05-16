package mok.it.app.mokapp.model

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig

object Collections {
    const val LINKS = "/links"
    const val REWARDS = "/rewards"
    const val USERS = "/users"
    const val COMMENTS = "/comments"
    const val ACHIEVMENTS = "/achievementsTest" //TODO: change this to "achievements" when done

    val REWARDREQUESTS = "rewardrequests" + Firebase.remoteConfig.getString("season")
    val PROJECTS: String = "projects" + Firebase.remoteConfig.getString("season")
}