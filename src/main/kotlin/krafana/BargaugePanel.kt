package krafana

import kotlinx.serialization.Serializable
import krafana.Calcs.last

@Serializable
data class BargaugePanel(
    override val datasource: DataSource,
    override val options: BargaugeOptions = BargaugeOptions(),
    override val targets: MutableList<Target> = mutableListOf(),
    override val fieldConfig: FieldConfig = FieldConfig(),
    override var title: String = "",
    var description: String = "",
    override var gridPos: GridPos = GridPos(0, 0, 12, 10),
    override var repeat: Expr? = null,
    override var repeatDirection: RepeatDirection? = null,
) : DataPanel<BargaugeOptions> {
    override val type: String = "bargauge"
}

@Serializable
enum class BarGaugeOrientation {
    horizontal,
    vertical
}

@Serializable
data class ReduceOptions (
    var calcs: MutableList<Calcs> = mutableListOf(last),
    var fields: String = ""
)
@Serializable
data class BargaugeOptions(
    var orientation: BarGaugeOrientation = BarGaugeOrientation.horizontal,
    var reduceOptions: ReduceOptions = ReduceOptions(),
    var displayMode: DisplayMode? = null
) : Options

@Serializable
enum class DisplayMode {
   basic, gradient, lcd
}
fun DashboardParams.bargauge(
    title: String? = null,
    builder: BargaugePanel.() -> Unit
) {
    this.dashboard.bargauge(this.datasource, gridPosSequence) {
        title?.let { this.title = it }
        builder(this)
    }
}

fun RowParams.bargauge(
    title: String? = null,
    width: Int? = null,
    height: Int? = null,
    builder: BargaugePanel.() -> Unit
) {
    this.dashboardParams.dashboard.panels += BargaugePanel(this.dashboardParams.datasource)
        .also { it.title = title ?: "" }
        .also { it.gridPos = this.dashboardParams.gridPosSequence.next(width, height) }
        .apply(builder)
}

fun Dashboard.bargauge(
    datasource: DataSource,
    gridPosSequence: GridPosSequence = constant(),
    builder: BargaugePanel.() -> Unit
) {
    this.panels += BargaugePanel(datasource)
        .also { it.gridPos = gridPosSequence.next() }
        .apply(builder)
}


fun BargaugePanel.reduceOptions(calcs: Calcs) {
    this.options.reduceOptions.calcs = mutableListOf(calcs)
}