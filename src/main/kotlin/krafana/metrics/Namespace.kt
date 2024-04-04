package krafana.metrics

import krafana.Metric
import java.util.concurrent.LinkedBlockingDeque


private fun List<String>.metric(name: String, vararg extra: String): Metric {
    var output = this.joinToString("_")
    if (name.isNotBlank()) output += "_${name}"
    if (extra.isNotEmpty()) output += "_" + extra.joinToString("_")
    return krafana.metric(output)
}

class Histogram(namespaces: List<String>, name: String) {
    val p99: Metric = namespaces.metric(name, "99pct")
    val p95: Metric = namespaces.metric(name, "95pct")
    val p75: Metric = namespaces.metric(name, "75pct")
    val median: Metric = namespaces.metric(name, "median")
    val avg: Metric = namespaces.metric(name, "avg")
    val max: Metric = namespaces.metric(name, "max")
    val stddev: Metric = namespaces.metric(name, "stddev")
    val count: Metric = namespaces.metric(name, "count")
}

class Counter(namespaces: List<String>, name: String) {
    val total: Metric = namespaces.metric(name, "total")
}

class Gauge(namespaces: List<String>, name: String) {
    val value: Metric = namespaces.metric(name)
}

class Meter(namespaces: List<String>, name: String) {
    val total: Metric = namespaces.metric(name, "total")
    val rate: Metric = namespaces.metric(name, "rate")
    val oneMinRate: Metric = namespaces.metric(name, "oneminrate")
    val meanRate: Metric = namespaces.metric(name, "meanrate")
}

fun Namespace.histogram(name: String = "") = Histogram(this.parentNames, name)
fun Namespace.counter(name: String = "") = Counter(this.parentNames, name)
fun Namespace.gauge(name: String = "") = Gauge(this.parentNames, name)
fun Namespace.meter(name: String = "") = Meter(this.parentNames, name)

abstract class Namespace(private val parent: Namespace?, val name: String) {
    internal val parentNames: List<String>

    init {
        val namespaces = LinkedBlockingDeque<String>()
        if (name.isNotBlank()) {
            namespaces += name
        }
        fun iterateOverParents(parent: Namespace?) {
            if (parent != null) {
                if (parent.name.isNotBlank()) {
                    namespaces.addFirst(parent.name)
                }
                iterateOverParents(parent.parent)
            }
        }
        iterateOverParents(parent)
        this.parentNames = namespaces.toList()
    }
}