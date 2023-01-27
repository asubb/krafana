package krafana

import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue
import kotlin.random.Random

@Serializable
data class Target(
    var datasource: DataSource,
    var legendFormat: LegendFormat? = null,
    var expr: Expr? = null,
    var refId: String = Random.nextInt().absoluteValue.toString(36),
    var hide: Boolean = false,
    var type: TargetType? = null,
    var expression: Expr? = null,
    var downsampler: DownsampleFunc? = null,
    var upsampler: UpsampleFunc? = null,
    var window: Time? = null
)

enum class TargetType {
    math,
    resample
}

fun TimeseriesPanel.target(
    datasource: DataSource = this.datasource,
    builder: Target.() -> Unit
): RefId {
    val target = Target(datasource)
    this.targets += target.apply(builder)
    return RefId(target.refId)
}

fun TimeseriesPanel.expression(
    builder: Target.() -> Unit
): RefId {
    val target = Target(DataSource.expression)
    this.targets += target.apply(builder)
    return RefId(target.refId)
}

fun Target.legend(legend: String, vararg labels: Expr) {
    if (datasource != DataSource.expression) {
        val labelsString = labels.joinToString(" ", prefix = "{", postfix = "}") {
            "${it.value}=\"{{${it.value}}}\""
        }
        this.legendFormat = LegendFormat(legend +
                labelsString.let { if (it.isEmpty()) it else " $it" })
    } else {
        refId = legend
    }
}

fun Target.math(expr: Expr) {
    this.type = TargetType.math
    this.expression = expr
}

@Serializable(AsStringSerializer::class)
data class DownsampleFunc(val name: String): SerializableAsString {
    companion object {
        val last = DownsampleFunc("last")
        val mean = DownsampleFunc("mean")
        val min = DownsampleFunc("min")
        val max = DownsampleFunc("max")
        val sum = DownsampleFunc("sum")
    }

    override fun serialize(): String {
        return name
    }
}

@Serializable(AsStringSerializer::class)
data class UpsampleFunc(val name: String): SerializableAsString {
    companion object {
        val pad = UpsampleFunc("pad")
        val fillna = UpsampleFunc("fillna")
        val backfilling = UpsampleFunc("backfilling")
    }

    override fun serialize(): String {
        return name
    }
}

fun Target.resample(
    expr: Expr,
    window: Time,
    downsampler: DownsampleFunc = DownsampleFunc.mean,
    upsampler: UpsampleFunc = UpsampleFunc.fillna
) {
    this.type = TargetType.resample
    this.expression = expr
    this.downsampler = downsampler
    this.upsampler = upsampler
    this.window = window
}
