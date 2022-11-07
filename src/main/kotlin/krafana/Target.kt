package krafana

import kotlinx.serialization.SerialName
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
    var type: String? = null,
    var expression: Expr? = null,
)

fun TimeseriesPanel.target(
    datasource: DataSource = this.datasource,
    builder: Target.() -> Unit
): RefId {
    val target = Target(datasource)
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
    this.type = "math"
    this.expression = expr
}
