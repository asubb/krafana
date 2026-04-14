package krafana

import kotlinx.serialization.Serializable

@Serializable
data class HeatmapPanel(
    override val datasource: DataSource,
    override val options: HeatmapOptions = HeatmapOptions(),
    override val targets: MutableList<Target> = mutableListOf(),
    override val fieldConfig: FieldConfig = FieldConfig(),
    override var title: String = "",
    var description: String = "",
    override var gridPos: GridPos = GridPos(0, 0, 12, 10),
    override var repeat: Expr? = null,
    override var repeatDirection: RepeatDirection? = null,
) : DataPanel<HeatmapOptions> {
    override val type: String = "heatmap"
}

@Serializable
data class HeatmapOptions(
    var tooltip: ToolTip = ToolTip(),
    var legend: Legend = Legend(),
    var calculate: Boolean? = null,
) : Options

fun DashboardParams.heatmap(
    title: String? = null,
    width: Int? = null,
    height: Int? = null,
    builder: HeatmapPanel.() -> Unit,
) {
    this.dashboard.heatmap(
        this.datasource,
        gridPosSequence,
        title,
        width,
        height,
        builder
    )
}

fun RowParams.heatmap(
    title: String? = null,
    width: Int? = null,
    height: Int? = null,
    builder: HeatmapPanel.() -> Unit,
) {
    this.dashboardParams.dashboard.panels += HeatmapPanel(this.dashboardParams.datasource)
        .also { it.title = title ?: "" }
        .also { it.gridPos = this.dashboardParams.gridPosSequence.next(width, height) }
        .apply(builder)
}

fun Dashboard.heatmap(
    datasource: DataSource,
    gridPosSequence: GridPosSequence = constant(),
    title: String? = null,
    width: Int? = null,
    height: Int? = null,
    builder: HeatmapPanel.() -> Unit,
) {
    this.panels += HeatmapPanel(datasource)
        .also { it.title = title ?: "" }
        .also { it.gridPos = gridPosSequence.next(width, height) }
        .apply(builder)
}
