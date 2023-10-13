package mok.it.app.mokapp.firebase

import kotlinx.coroutines.flow.Flow
import mok.it.app.mokapp.model.User

typealias Users = List<User>
interface IFirebaseRepository {
    fun getBadgeMembers(): Flow<Users>
}