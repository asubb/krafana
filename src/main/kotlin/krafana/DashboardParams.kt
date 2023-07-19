package krafana

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

