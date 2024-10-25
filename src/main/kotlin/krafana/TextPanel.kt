package krafana

import kotlinx.serialization.Serializable

@Serializable
data class TextPanel(
    override val datasource: DataSource,
    override val options: TextOptions = TextOptions(),
    override var title: String = "",
    var description: String = "",
    override var gridPos: GridPos = GridPos(0, 0, 12, 10),
    override var repeat: Expr? = null,
    override var repeatDirection: RepeatDirection? = null,
    var interval: Time? = null,
) : Panel<TextOptions> {
    override val type: String = "text"
}

@Serializable
data class TextOptions(
    var language: String = "plaintext",
    var showLineNumbers: Boolean = false,
    var showMiniMap: Boolean = false,
    var content: String = "",
): Options

fun DashboardParams.text(
    title: String? = null,
    width: Int? = null,
    height: Int? = null,
    builder: TextPanel.() -> Unit,
) {
    this.dashboard.text(
        this.datasource,
        gridPosSequence,
        title,
        width,
        height,
        builder
    )
}

fun RowParams.text(
    title: String? = null,
    width: Int? = null,
    height: Int? = null,
    builder: TextPanel.() -> Unit,
) {
    this.dashboardParams.dashboard.panels += TextPanel(this.dashboardParams.datasource)
        .also { it.title = title ?: "" }
        .also { it.gridPos = this.dashboardParams.gridPosSequence.next(width, height) }
        .apply(builder)
}

fun Dashboard.text(
    datasource: DataSource,
    gridPosSequence: GridPosSequence = constant(),
    title: String? = null,
    width: Int? = null,
    height: Int? = null,
    builder: TextPanel.() -> Unit,
) {
    this.panels += TextPanel(datasource)
        .also { it.title = title ?: "" }
        .also { it.gridPos = gridPosSequence.next(width, height) }
        .apply(builder)
}

var TextPanel.content: String
    get() = options.content
    set(value) { options.content = value}