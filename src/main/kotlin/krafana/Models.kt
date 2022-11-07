package krafana

import kotlinx.serialization.Serializable

@Serializable
data class GridPos(
    var x: Int,
    var y: Int,
    var w: Int,
    var h: Int
)

enum class Style {
    default, dark, light
}

enum class Timezone {
    browser, utc,
}

