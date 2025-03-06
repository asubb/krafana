package krafana

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

@Serializable
data class TimeseriesPanel(
    override val datasource: DataSource,
    override val options: TimeseriesOptions = TimeseriesOptions(),
    override val targets: MutableList<Target> = mutableListOf(),
    override val fieldConfig: FieldConfig = FieldConfig(),
    override var title: String = "",
    var description: String = "",
    override var gridPos: GridPos = GridPos(0, 0, 12, 10),
    override var repeat: Expr? = null,
    override var repeatDirection: RepeatDirection? = null,
    var interval: Time? = null,
) : DataPanel<TimeseriesOptions> {
    override val type: String = "timeseries"
}


@Serializable
data class TimeseriesOptions(
    var tooltip: ToolTip = ToolTip(),
    var legend: Legend = Legend(),
) : Options

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

fun TimeseriesPanel.override(builder: FieldOverride.() -> Unit) {
    this.fieldConfig.overrides += FieldOverride().apply(builder)
}

fun FieldOverride.match(type: MatcherType, builder: () -> String?) {
    matcher.id = type
    matcher.options = builder()
}

fun <T> FieldOverride.property(id: String, builder: () -> T) {
    properties += PropertyOverride(
        id,
        Json.encodeToJsonElement(AnySerializer, builder())
    )
}

object AnySerializer : kotlinx.serialization.KSerializer<Any?> {
    override val descriptor = String.serializer().descriptor

    override fun serialize(encoder: kotlinx.serialization.encoding.Encoder, value: Any?) {
        val jsonEncoder = encoder as? kotlinx.serialization.json.JsonEncoder
            ?: throw IllegalStateException("Can only encode JSON")
        when (value) {
            is String -> jsonEncoder.encodeString(value)
            is Number -> jsonEncoder.encodeDouble(value.toDouble())
            is Boolean -> jsonEncoder.encodeBoolean(value)
            is Map<*, *> -> jsonEncoder.encodeSerializableValue(
                MapSerializer(String.serializer(), AnySerializer),
                value as Map<String, Any?>
            )

            is List<*> -> jsonEncoder.encodeSerializableValue(
                kotlinx.serialization.builtins.ListSerializer(AnySerializer),
                value
            )

            null -> jsonEncoder.encodeNull()
            else -> jsonEncoder.encodeString(value.toString()) // Fallback for unsupported types
        }
    }

    override fun deserialize(decoder: kotlinx.serialization.encoding.Decoder): Any? {
        throw NotImplementedError("Deserialization not supported")
    }
}

fun TimeseriesPanel.thresholds(builder: Thresholds.() -> Unit) {
    if (fieldConfig.defaults.thresholds == null) {
        fieldConfig.defaults.thresholds = Thresholds()
    }
    builder(this.fieldConfig.defaults.thresholds!!)
}
