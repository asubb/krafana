package krafana

import kotlinx.serialization.Serializable

@Serializable
data class TablePanel(
    override val datasource: DataSource,
    override val options: Options = Options(),
    override val targets: MutableList<Target> = mutableListOf(),
    override val fieldConfig: FieldConfig = FieldConfig(),
    override var title: String = "",
    override var gridPos: GridPos = GridPos(0, 0, 12, 10),
    override var repeat: Expr? = null,
    override var repeatDirection: RepeatDirection? = null,
    var interval: Time? = null,
    var description: String = "",
)
: Panel<Options> {
    override val type: String = "table"
}

fun DashboardParams.table(
    title: String? = null,
    width: Int? = null,
    height: Int? = null,
    builder: TablePanel.() -> Unit,
) {
    this.dashboard.table(
        this.datasource,
        gridPosSequence,
        title,
        width,
        height,
        builder
    )
}

fun RowParams.table(
    title: String? = null,
    width: Int? = null,
    height: Int? = null,
    builder: TablePanel.() -> Unit,
) {
    this.dashboardParams.dashboard.panels += TablePanel(this.dashboardParams.datasource)
        .also { it.title = title ?: "" }
        .also { it.gridPos = this.dashboardParams.gridPosSequence.next(width, height) }
        .apply(builder)
}

fun Dashboard.table(
    datasource: DataSource,
    gridPosSequence: GridPosSequence = constant(),
    title: String? = null,
    width: Int? = null,
    height: Int? = null,
    builder: TablePanel.() -> Unit,
) {
    this.panels += TablePanel(datasource)
        .also { it.title = title ?: "" }
        .also { it.gridPos = gridPosSequence.next(width, height) }
        .apply(builder)
}

fun TablePanel.config(builder: CustomConfig.() -> Unit) {
    builder(this.fieldConfig.defaults.custom)
}
