package mok.it.app.mokapp.model

data class Filter(
    var achieved: Boolean = false,
    var mandatory: Boolean = false,
    var joined: Boolean = false,
    var edited: Boolean = false,
) : java.io.Serializable
