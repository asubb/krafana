package krafana

operator fun Expr.div(other: Expr): Expr {
    return Expr("(${this.value}) / (${other.value})")
}

operator fun Expr.times(other: Number): Expr {
    return Expr("(${this.value}) * ($other)")
}

operator fun Expr.times(other: Expr): Expr {
    return Expr("(${this.value}) * (${other.value})")
}


