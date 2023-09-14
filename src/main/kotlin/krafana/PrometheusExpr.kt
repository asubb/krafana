package krafana

infix fun Expr.eq(v: String): Filter {
    return Filter(this, value(v), "=")
}

infix fun Expr.eq(v: Expr): Filter {
    return Filter(this, v, "=")
}

infix fun Expr.re(v: Expr): Filter {
    return Filter(this, v, "=~")
}

infix fun Expr.ne(v: String): Filter {
    return Filter(this, value(v), "!=")
}

operator fun Expr.minus(other: Expr): Expr {
    return Expr("${this.value}-${other.value}")
}

fun Expr.filter(vararg filters: Filter): Expr {
    return Expr("${this.value}{${filters.joinToString(",") { it.value }}}")
}

fun Expr.rate(interval: Time): Expr {
    return Expr("rate(${this.value}[$interval])")
}

fun Expr.count(): Expr = countBy()

fun Expr.countBy(vararg by: Expr): Expr {
    val byString = by.takeIf { it.isNotEmpty() }
        ?.joinToString(",", prefix = " by (", postfix = ")") { it.value }
        ?: ""
    return Expr("count$byString(${this.value})")
}

fun Expr.sum(): Expr = sumBy()

fun Expr.sumBy(vararg by: Expr): Expr {
    val byString = by.takeIf { it.isNotEmpty() }
        ?.joinToString(",", prefix = " by (", postfix = ")") { it.value }
        ?: ""
    return Expr("sum$byString(${this.value})")
}

fun Expr.avg(): Expr = avgBy()

fun Expr.avgBy(vararg by: Expr): Expr {
    val byString = by.takeIf { it.isNotEmpty() }
        ?.joinToString(",", prefix = " by (", postfix = ")") { it.value }
        ?: ""
    return Expr("avg$byString(${this.value})")
}

fun Expr.changes(range: Time): Expr {
    return Expr("changes(${this.value}[$range])")
}

fun Expr.increase(range: Time): Expr {
    return Expr("increase(${this.value}[$range])")
}

fun Expr.delta(range: Time): Expr {
    return Expr("delta(${this.value}[$range])")
}

/**
 * [Prometheus delta function](https://prometheus.io/docs/prometheus/latest/querying/functions/#delta)
 * with sampling interval as a parameter
 */
fun Expr.deltaInterval(): Expr {
    return Expr("delta(${this.value}[\$__interval])")
}

fun Expr.idelta(range: Time): Expr {
    return Expr("idelta(${this.value}[$range])")
}

fun Expr.ideltaInterval(): Expr {
    return Expr("idelta(${this.value}[\$__interval])")
}

fun Expr.abs(): Expr {
    return Expr("abs($this)")
}

fun Metric.labelValues(label: Label): Expr {
    return Expr("label_values(${this.value}, ${label.value})")
}

fun labelValues(label: Label): Expr {
    return Expr("label_values(${label.value})")
}

fun Expr.quantileOverTime(quantile: Double, time: Time): Expr {
    return Expr("quantile_over_time($quantile, $this[$time]")
}

