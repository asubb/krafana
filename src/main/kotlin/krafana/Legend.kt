package krafana

import kotlinx.serialization.Serializable

@Serializable
data class Legend(
    val calcs: MutableList<Calcs> = mutableListOf(),
    var displayMode: LegendDisplayMode = LegendDisplayMode.table,
    var showLegend: Boolean = true,
    var placement: LegendPlacement = LegendPlacement.bottom
)

@Serializable
enum class LegendPlacement {
    bottom, right
}
@Serializable
enum class LegendDisplayMode {
    table, list
}