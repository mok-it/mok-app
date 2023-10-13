package mok.it.app.mokapp.firebase

import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepository @Inject constructor(
    private val usersRef: CollectionReference
): IFirebaseRepository {
    override fun getBadgeMembers(): callbackFlow {
        TODO("Not yet   implemented")
    }

}