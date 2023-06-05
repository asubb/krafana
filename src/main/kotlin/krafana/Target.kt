package krafana

import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue
import kotlin.random.Random

@Serializable
open class Target(
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
) {
    override fun toString(): String {
        return "Target(datasource=$datasource, legendFormat=$legendFormat, expr=$expr, refId='$refId', hide=$hide, type=$type, expression=$expression, downsampler=$downsampler, upsampler=$upsampler, window=$window)"
    }
}

enum class TargetType {
    math,
    resample
}

@Serializable(AsStringSerializer::class)
data class DownsampleFunc(val name: String) : SerializableAsString {
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
data class UpsampleFunc(val name: String) : SerializableAsString {
    companion object {
        val pad = UpsampleFunc("pad")
        val fillna = UpsampleFunc("fillna")
        val backfilling = UpsampleFunc("backfilling")
    }

    override fun serialize(): String {
        return name
    }
}

class ExpressionTarget(datasource: DataSource) : Target(datasource)

fun TimeseriesPanel.query(
    datasource: DataSource = this.datasource,
    builder: Target.() -> Unit
): RefId {
    val target = Target(datasource)
    this.targets += target.apply(builder)
    return RefId(target.refId)
}

fun TimeseriesPanel.expression(
    type: TargetType,
    builder: ExpressionTarget.() -> Unit
): RefId {
    val target = ExpressionTarget(DataSource.expression)
    target.type = type
    this.targets += target.apply(builder)
    return RefId(target.refId)
}

fun Target.legend(legend: String, vararg labels: Expr) {
    if (datasource != DataSource.expression) {
        val labelsString =
            if (labels.isEmpty()) ""
            else labels.joinToString(" ", prefix = "{", postfix = "}") {
                "${it.value}=\"{{${it.value}}}\""
            }
        this.legendFormat = LegendFormat(legend +
                labelsString.let { if (it.isEmpty()) it else " $it" })
    } else {
        refId = legend
    }
}

fun TimeseriesPanel.mathExpression(
    expr: Expr,
    builder: (ExpressionTarget.() -> Unit)? = null
) {
    this.expression(TargetType.math) {
        expression = expr
        builder?.invoke(this)
    }
}

fun TimeseriesPanel.resampleExpression(
    expr: Expr,
    window: Time,
    downsampler: DownsampleFunc = DownsampleFunc.mean,
    upsampler: UpsampleFunc = UpsampleFunc.fillna,
    builder: (ExpressionTarget.() -> Unit)? = null,
): RefId {
    return this.expression(TargetType.resample) {
        this.expression = expr
        this.downsampler = downsampler
        this.upsampler = upsampler
        this.window = window
        builder?.invoke(this)
    }
}
