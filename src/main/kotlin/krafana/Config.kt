package krafana

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    var showPoints: ShowPoints = ShowPoints.auto
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
    var mode: StackingMode = StackingMode.None
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
    val format: String
) : SerializableAsString {
    override fun serialize(): String {
        return format
    }
}