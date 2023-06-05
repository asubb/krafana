package krafana

import kotlinx.serialization.Serializable

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = AsStringSerializer::class)
open class Expr(
    val value: String
) : SerializableAsString {

    override fun serialize(): String {
        return value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Expr

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return value
    }
}

class Filter(operand1: Expr, operand2: Expr, operator: String) :
    Expr("$operand1$operator$operand2")

class Var(val name: String) : Expr("\"$$name\"")

class Label(name: String) : Expr(name)

class Metric(name: String) : Expr(name)

class RefId(ref: String) : Expr("$$ref")

fun variable(name: String): Var = Var(name)

fun metric(name: String): Metric = Metric(name)

fun value(v: String): Expr = Expr("\"$v\"")

fun label(label: String): Label = Label(label)

