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
    var window: Time? = null,
    var reducer: ReducerFunc? = null,
    var settings: TargetSettings = TargetSettings()
) {
    override fun toString(): String {
        return "Target(datasource=$datasource, legendFormat=$legendFormat, expr=$expr, refId='$refId', hide=$hide, type=$type, expression=$expression, downsampler=$downsampler, upsampler=$upsampler, window=$window)"
    }
}

@Serializable
data class TargetSettings(
    var mode: TargetSettingsMode = TargetSettingsMode.dropNN
)

@Serializable
enum class TargetSettingsMode {
    dropNN,
}

enum class TargetType {
    math,
    resample,
    reduce
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

@Serializable(AsStringSerializer::class)
data class ReducerFunc(val name: String) : SerializableAsString {
    companion object {
        val max = ReducerFunc("max")
        val mean = ReducerFunc("mean")
        val sum = ReducerFunc("sum")
    }

    override fun serialize(): String {
        return name
    }
}
class ExpressionTarget(datasource: DataSource) : Target(datasource)

fun Panel<*>.query(
    datasource: DataSource = this.datasource,
    builder: Target.() -> Unit
): RefId {
    val target = Target(datasource)
    this.targets += target.apply(builder)
    return RefId(target.refId)
}

fun Panel<*>.expression(
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

fun Panel<*>.mathExpression(
    expr: Expr,
    builder: (ExpressionTarget.() -> Unit)? = null
): RefId {
    return this.expression(TargetType.math) {
        expression = expr
        builder?.invoke(this)
    }
}

fun Panel<*>.reduceExpression(
    expr: Expr,
    reducer: ReducerFunc,
    builder: (ExpressionTarget.() -> Unit)? = null
): RefId {
    return this.expression(TargetType.reduce) {
        this.expression = expr
        this.reducer = reducer
        builder?.invoke(this)
    }
}

fun Panel<*>.resampleExpression(
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
