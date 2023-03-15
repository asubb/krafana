package krafana

import kotlinx.serialization.Serializable

@Serializable
data class Folder(
    val id: String,
    val uid: String,
    val title: String
)