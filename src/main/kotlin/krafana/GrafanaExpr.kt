package krafana

operator fun Expr.div(other: Expr): Expr {
    return Expr("(${this.value}) / (${other.value})")
}

operator fun Expr.plus(other: Expr): Expr {
    return Expr("(${this.value}) + (${other.value})")
}

infix fun Expr.or(other: Expr): Expr {
    return Expr("(${this.value}) || (${other.value})")
}

infix fun Expr.equal(other: Expr): Expr {
    return Expr("(${this.value}) == (${other.value})")
}
operator fun Expr.times(other: Number): Expr {
    return Expr("(${this.value}) * ($other)")
}

operator fun Expr.times(other: Expr): Expr {
    return Expr("(${this.value}) * (${other.value})")
}

operator fun Expr.unaryMinus():Expr {
    return Expr("(-${this.value})")
}

fun isNan(e: Expr): Expr {
    return Expr("is_nan(${e.value})")
}