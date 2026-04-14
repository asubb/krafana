package krafana

import kotlinx.serialization.Serializable

sealed interface Options

@Serializable
data class CommonOptions(
    var tooltip: ToolTip = ToolTip(),
    var legend: Legend = Legend(),
): Options