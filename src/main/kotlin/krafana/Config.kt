package krafana

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Config(
    var unit: Measure = Measure.none,
    var custom: CustomConfig = CustomConfig(),
    var color: ColorConfig = ColorConfig(),
    var thresholds: Thresholds? = null,
)

@Serializable
data class Thresholds(
    var mode: String = "absolute",
    var steps: MutableList<Step> = mutableListOf<Step>(),
)

@Serializable
data class Step(
    val color: String,
    val value: Int?,
)

@Serializable
data class CustomConfig(
    var fillOpacity: Double = 0.0,
    var drawStyle: DrawStyle = DrawStyle.Lines,
    var stacking: Stacking = Stacking(),
    var axisCenteredZero: Boolean? = null,
    var axisSoftMin: Int? = null,
    var axisSoftMax: Int? = null,
    var gradientMode: GradientMode = GradientMode.none,
    var showPoints: ShowPoints = ShowPoints.auto,
)

@Serializable
enum class ShowPoints {
    never,
    always,
    auto
}

@Serializable
enum class GradientMode {
    none,
    opacity,
    hue,
    scheme
}

@Serializable
data class Stacking(
    var group: String = "A",
    var mode: StackingMode = StackingMode.None,
)

@Serializable
enum class StackingMode {
    @SerialName("none")
    None,

    @SerialName("normal")
    Normal,

    @SerialName("percent")
    Percent
}

@Serializable
enum class DrawStyle {
    @SerialName("bars")
    Bars,

    @SerialName("lines")
    Lines,

    @SerialName("points")
    Points
}

@Serializable
data class FieldConfig(
    var defaults: Config = Config(),
    var overrides: MutableList<FieldOverride> = mutableListOf(),
)

@Serializable
data class FieldOverride(
    var matcher: Matcher = Matcher(),
    var properties: MutableList<PropertyOverride> = mutableListOf(),
)

@Serializable
data class Matcher(
    var id: MatcherType = MatcherType.byName,
    var options: String? = null,
)

/**
 * Represents the possible matcher types for applying overrides in Grafana.
 * Each matcher specifies the fields or series that a specific override rule applies to.
 */
@Serializable
enum class MatcherType {

    /**
     * Matches fields by their **name** as they appear in the legend.
     */
    @SerialName("byName")
    byName,

    /**
     * Matches fields by the **RefID** of the query (e.g., `"A"`, `"B"`, etc.).
     */
    @SerialName("byFrameRefID")
    byFrameRefId,

    /**
     * Matches fields using a **regular expression** on their names.
     */
    @SerialName("byRegex")
    byRegex,

    /**
     * Matches fields based on their **value type** (e.g., numeric, string, etc.).
     */
    @SerialName("byType")
    byType,

    /**
     * Matches fields by their **exact internal field name**.
     */
    @SerialName("byFieldName")
    byFieldName,

    /**
     * Matches fields based on their **null value mode** (e.g., Null as Zero).
     */
    @SerialName("byNullValueMode")
    byNullValueMode
}

@Serializable
data class PropertyOverride(
    var id: String,
    var value: JsonElement? = null,
)

@Serializable
data class ColorConfig(
    var mode: ColorConfigMode = ColorConfigMode.palleteClassic,
)

@Serializable
enum class ColorConfigMode {
    @SerialName("palette-classic")
    palleteClassic,

    @SerialName("thresholds")
    thresholds,

    @SerialName("continuous-GrYlRd")
    continuousGrYlRd,

    @SerialName("continuous-BlPu")
    continuousBlPu,
}

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(AsStringSerializer::class)
class LegendFormat(
    val format: String,
) : SerializableAsString {
    override fun serialize(): String {
        return format
    }
}