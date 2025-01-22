package krafana

import kotlinx.serialization.Serializable

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = AsStringSerializer::class)
data class Time(
    val v: Int,
    val u: String,
    val v2: Int? = null,
    val u2: String? = null,
) : SerializableAsString {
    override fun serialize(): String {
        val vs = if (v > 0) "$v" else ""
        return if (u2 != null && v2 != null) {
            val v2s = if (v2 > 0) "$v2" else ""
            "${vs}${u}-${v2s}${u2}"
        } else {
            "${vs}${u}"
        }
    }

    override fun toString(): String {
        return serialize()
    }
}

@Serializable
data class TimeRange(
    val from: Time,
    val to: Time
)

val Int.s
    get():Time = Time(this, "s")
val Int.m
    get():Time = Time(this, "m")
val Int.h
    get():Time = Time(this, "h")
val Int.d
    get():Time = Time(this, "d")
val now
    get(): Time = Time(0, "now")

operator fun Time.minus(to: Time) = Time(this.v, this.u, to.v, to.u)

operator fun Time.rangeTo(to: Time): TimeRange = TimeRange(this, to)

operator fun Time.unaryMinus(): Time = Time(-this.v, this.u, this.v2?.let { -it }, this.u2)
