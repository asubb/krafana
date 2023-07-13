package krafana

import kotlinx.serialization.Serializable

@Serializable
data class RowPanel(
    override var title: String,
    override var gridPos: GridPos,
    var panels: MutableList<Panel>,
): Panel {
    override val type: String = "row"
    override val datasource: DataSource
        get() = throw UnsupportedOperationException()
    override val targets: MutableList<Target>
        get() = throw UnsupportedOperationException()
    override val fieldConfig: FieldConfig = FieldConfig()
    override var repeat: Expr?
        get() = throw UnsupportedOperationException()
        set(value) = throw UnsupportedOperationException()
    override var repeatDirection: RepeatDirection?
        get() = throw UnsupportedOperationException()
        set(value) = throw UnsupportedOperationException()
}

data class RowParams(
    val row: RowPanel,
    val dashboardParams: DashboardParams,
)

fun DashboardParams.row(
    title: String,
    builder: RowParams.() -> Unit
) {
    val row = RowPanel(title, this.fullWidth(), mutableListOf())
    val params = RowParams(row, this)
    this.dashboard.panels += row
    builder.invoke(params)
}