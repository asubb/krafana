package krafana

import kotlinx.serialization.Serializable

@Serializable
enum class Calcs {
    last,
    mean,
    sum,
    max,
}