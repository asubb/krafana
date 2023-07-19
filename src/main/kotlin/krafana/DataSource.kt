package krafana

import kotlinx.serialization.Serializable

@Serializable
data class DataSource(
    var type: String,
    var uid: String
) {
    companion object {
        val expression: DataSource = DataSource("__expr__", "__expr__")

        fun prometheus(uid: String) = DataSource("prometheus", uid)
    }
}