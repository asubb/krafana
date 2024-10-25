package krafana

import kotlinx.serialization.Serializable

@Serializable
data class TimeseriesPanel(
    override val datasource: DataSource,
    override val options: CommonOptions = CommonOptions(),
    override val targets: MutableList<Target> = mutableListOf(),
    override val fieldConfig: FieldConfig = FieldConfig(),
    override var title: String = "",
    var description: String = "",
    override var gridPos: GridPos = GridPos(0, 0, 12, 10),
    override var repeat: Expr? = null,
    override var repeatDirection: RepeatDirection? = null,
    var interval: Time? = null,
) : DataPanel<CommonOptions> {
    override val type: String = "timeseries"
}

fun DashboardParams.timeseries(
    title: String? = null,
    drawStyle: DrawStyle? = null,
    width: Int? = null,
    height: Int? = null,
    builder: TimeseriesPanel.() -> Unit,
) {
    this.dashboard.timeseries(
        this.datasource,
        gridPosSequence,
        title,
        drawStyle,
        width,
        height,
        builder
    )
}

fun RowParams.timeseries(
    title: String? = null,
    drawStyle: DrawStyle? = null,
    width: Int? = null,
    height: Int? = null,
    builder: TimeseriesPanel.() -> Unit,
) {
    this.dashboardParams.dashboard.panels += TimeseriesPanel(this.dashboardParams.datasource)
        .also { it.title = title ?: "" }
        .also { it.gridPos = this.dashboardParams.gridPosSequence.next(width, height) }
        .also { if (drawStyle != null) it.fieldConfig.defaults.custom.drawStyle = drawStyle }
        .apply(builder)
}

fun Dashboard.timeseries(
    datasource: DataSource,
    gridPosSequence: GridPosSequence = constant(),
    title: String? = null,
    drawStyle: DrawStyle? = null,
    width: Int? = null,
    height: Int? = null,
    builder: TimeseriesPanel.() -> Unit,
) {
    this.panels += TimeseriesPanel(datasource)
        .also { it.title = title ?: "" }
        .also { it.gridPos = gridPosSequence.next(width, height) }
        .also { if (drawStyle != null) it.fieldConfig.defaults.custom.drawStyle = drawStyle }
        .apply(builder)
}

fun TimeseriesPanel.config(builder: CustomConfig.() -> Unit) {
    builder(this.fieldConfig.defaults.custom)
}