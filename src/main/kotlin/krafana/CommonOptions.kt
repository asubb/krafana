package krafana

import kotlinx.serialization.Serializable

interface Options {

}
@Serializable
data class CommonOptions(
    var tooltip: ToolTip = ToolTip(),
    var legend: Legend = Legend(),
): Options