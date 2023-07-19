package krafana

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ToolTip(
    var mode: ToolTipMode = ToolTipMode.Multi,
    var sort: ToolTipSort = ToolTipSort.None,
)

@Serializable
enum class ToolTipSort {
    @SerialName("none")
    None,
}

@Serializable
enum class ToolTipMode {
    @SerialName("single")
    Single,

    @SerialName("multi")
    Multi
}

var Panel<Options>.toolTipMode
    get() = this.options.tooltip.mode
    set(value) {
        this.options.tooltip.mode = value
    }

