package krafana

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    var unit: Measure = Measure.none,
    var custom: CustomConfig = CustomConfig(),
    var color: ColorConfig = ColorConfig(),
)

@Serializable
data class CustomConfig(
    var fillOpacity: Double = 0.0,
    var drawStyle: DrawStyle = DrawStyle.Lines,
    var stacking: Stacking = Stacking()
)

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
    var mode: String = "palette-classic"
)

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(AsStringSerializer::class)
class LegendFormat(
    val format: String
) : SerializableAsString {
    override fun serialize(): String {
        return format
    }
}