package krafana

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimeseriesPanel(
    override val datasource: DataSource,
    val options: Options = Options(),
    override val targets: MutableList<Target> = mutableListOf(),
    override val fieldConfig: FieldConfig = FieldConfig(),
    override var title: String = "",
    var description: String = "",
    override var gridPos: GridPos = GridPos(0, 0, 12, 10),
) : Panel {
    override val type: String = "timeseries"
}

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

data class DashboardParams(
    val dashboard: Dashboard,
    val datasource: DataSource,
    var gridPosSequence: GridPosSequence,
)

fun Dashboard.with(
    datasource: DataSource,
    gridPosSequence: GridPosSequence = constant(),
    builder: DashboardParams.() -> Unit
) {
    DashboardParams(this, datasource, gridPosSequence).apply(builder)
}

fun DashboardParams.timeseries(
    title: String? = null,
    builder: TimeseriesPanel.() -> Unit
) {
    this.dashboard.timeseries(this.datasource, gridPosSequence) {
        title?.let { this.title = it }
        builder(this)
    }
}

fun RowParams.timeseries(
    title: String? = null,
    builder: TimeseriesPanel.() -> Unit
) {
    this.dashboardParams.dashboard.panels += TimeseriesPanel(this.dashboardParams.datasource)
        .also { it.title = title ?: "" }
        .also { it.gridPos = this.dashboardParams.gridPosSequence.next() }
        .apply(builder)
}

fun Dashboard.timeseries(
    datasource: DataSource,
    gridPosSequence: GridPosSequence = constant(),
    builder: TimeseriesPanel.() -> Unit
) {
    this.panels += TimeseriesPanel(datasource)
        .also { it.gridPos = gridPosSequence.next() }
        .apply(builder)
}

var TimeseriesPanel.toolTipMode
    get() = this.options.tooltip.mode
    set(value) {
        this.options.tooltip.mode = value
    }

fun TimeseriesPanel.config(builder: CustomConfig.() -> Unit) {
    builder(this.fieldConfig.defaults.custom)
}