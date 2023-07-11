package krafana

import kotlinx.serialization.Serializable
import krafana.ReduceOptionsCalcs.last

@Serializable
data class BarGaugePanel(
    override val datasource: DataSource,
    val options: BarGaugeOptions = BarGaugeOptions(),
    override val targets: MutableList<Target> = mutableListOf(),
    override val fieldConfig: FieldConfig = FieldConfig(),
    override var title: String = "",
    var description: String = "",
    override var gridPos: GridPos = GridPos(0, 0, 12, 10),
) : Panel {
    override val type: String = "bargauge"
}

@Serializable
enum class ReduceOptionsCalcs {
    last,
    mean,
    sum,
    max,
}

@Serializable
enum class BarGaugeOrientation {
    horizontal,
    vertical
}
@Serializable
data class ReduceOptions (
    var calcs: MutableList<ReduceOptionsCalcs> = mutableListOf(last),
    var fields: String = ""
)
@Serializable
data class BarGaugeOptions(
    var orientation: BarGaugeOrientation = BarGaugeOrientation.horizontal,
    var reduceOptions: ReduceOptions = ReduceOptions(),
)

@Serializable
data class Options(
    var tooltip: ToolTip = ToolTip(),
)

fun DashboardParams.bargauge(
    title: String? = null,
    builder: BarGaugePanel.() -> Unit
) {
    this.dashboard.bargauge(this.datasource, gridPosSequence) {
        title?.let { this.title = it }
        builder(this)
    }
}

fun RowParams.bargauge(
    title: String? = null,
    builder: BarGaugePanel.() -> Unit
) {
    this.dashboardParams.dashboard.panels += BarGaugePanel(this.dashboardParams.datasource)
        .also { it.title = title ?: "" }
        .also { it.gridPos = this.dashboardParams.gridPosSequence.next() }
        .apply(builder)
}

fun Dashboard.bargauge(
    datasource: DataSource,
    gridPosSequence: GridPosSequence = constant(),
    builder: BarGaugePanel.() -> Unit
) {
    this.panels += BarGaugePanel(datasource)
        .also { it.gridPos = gridPosSequence.next() }
        .apply(builder)
}


fun BarGaugePanel.reduceOptions(calcs: ReduceOptionsCalcs) {
    this.options.reduceOptions.calcs = mutableListOf(calcs)
}