package mok.it.app.mokapp.interfaces

interface UserRefresher {
    fun refreshUser(listener: UserRefreshedListener)
}