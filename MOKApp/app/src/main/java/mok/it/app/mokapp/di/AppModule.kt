package mok.it.app.mokapp.di

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.dagger.Module
import com.google.firebase.functions.dagger.Provides
import com.google.firebase.ktx.Firebase
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mok.it.app.mokapp.model.Collections


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideUsersRef() = Firebase.firestore.collection(Collections.users)
}