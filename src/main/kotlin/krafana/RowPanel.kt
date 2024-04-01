package krafana

import kotlinx.serialization.Serializable

@Serializable
data class RowPanel(
    override var title: String,
    override var gridPos: GridPos,
): Panel<Any> {
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
    override val options: Options
        get() = throw UnsupportedOperationException()
}

data class RowParams(
    val row: RowPanel,
    val dashboardParams: DashboardParams,
)

fun DashboardParams.row(
    title: String,
    builder: RowParams.() -> Unit
) {
    this.gridPosSequence.closeRow()
    val row = RowPanel(title, this.fullWidth())
    val params = RowParams(row, this)
    this.dashboard.panels += row
    builder.invoke(params)
}