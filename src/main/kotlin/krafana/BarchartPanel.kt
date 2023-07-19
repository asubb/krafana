package krafana

import kotlinx.serialization.Serializable

@Serializable
data class BarchartPanel(
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
    override val type: String = "barchart"
}

fun DashboardParams.barchart(
    title: String? = null,
    builder: BarchartPanel.() -> Unit
) {
    this.dashboard.barchart(this.datasource, gridPosSequence) {
        title?.let { this.title = it }
        builder(this)
    }
}

fun RowParams.barchart(
    title: String? = null,
    builder: BarchartPanel.() -> Unit
) {
    this.dashboardParams.dashboard.panels += BarchartPanel(this.dashboardParams.datasource)
        .also { it.title = title ?: "" }
        .also { it.gridPos = this.dashboardParams.gridPosSequence.next() }
        .apply(builder)
}

fun Dashboard.barchart(
    datasource: DataSource,
    gridPosSequence: GridPosSequence = constant(),
    builder: BarchartPanel.() -> Unit
) {
    this.panels += BarchartPanel(datasource)
        .also { it.gridPos = gridPosSequence.next() }
        .apply(builder)
}
