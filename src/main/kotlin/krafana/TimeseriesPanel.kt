package krafana

import kotlinx.serialization.Serializable

@Serializable
data class TimeseriesPanel(
    override val datasource: DataSource,
    override val options: Options = Options(),
    override val targets: MutableList<Target> = mutableListOf(),
    override val fieldConfig: FieldConfig = FieldConfig(),
    override var title: String = "",
    var description: String = "",
    override var gridPos: GridPos = GridPos(0, 0, 12, 10),
    override var repeat: Expr? = null,
    override var repeatDirection: RepeatDirection? = null,
) : Panel<Options> {
    override val type: String = "timeseries"
}

fun DashboardParams.timeseries(
    title: String? = null,
    builder: TimeseriesPanel.() -> Unit,
) {
    this.dashboard.timeseries(this.datasource, gridPosSequence) {
        title?.let { this.title = it }
        builder(this)
    }
}

fun RowParams.timeseries(
    title: String? = null,
    drawStyle: DrawStyle? = null,
    builder: TimeseriesPanel.() -> Unit,
) {
    this.dashboardParams.dashboard.panels += TimeseriesPanel(this.dashboardParams.datasource)
        .also { it.title = title ?: "" }
        .also { it.gridPos = this.dashboardParams.gridPosSequence.next() }
        .also { if (drawStyle != null) it.fieldConfig.defaults.custom.drawStyle = drawStyle }
        .apply(builder)
}

fun Dashboard.timeseries(
    datasource: DataSource,
    gridPosSequence: GridPosSequence = constant(),
    builder: TimeseriesPanel.() -> Unit,
) {
    this.panels += TimeseriesPanel(datasource)
        .also { it.gridPos = gridPosSequence.next() }
        .apply(builder)
}

fun TimeseriesPanel.config(builder: CustomConfig.() -> Unit) {
    builder(this.fieldConfig.defaults.custom)
}