package krafana

import kotlinx.serialization.Serializable

@Serializable
data class Options(
    var tooltip: ToolTip = ToolTip(),
    var legend: Legend = Legend(),
)