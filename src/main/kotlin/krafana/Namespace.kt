package krafana

import java.util.concurrent.LinkedBlockingDeque


private fun List<String>.metric(name: String, vararg extra: String): Metric {
    var output = this.joinToString("_")
    if (name.isNotBlank()) output += "_${name}"
    if (extra.isNotEmpty()) output += "_" + extra.joinToString("_")
    return krafana.metric(output)
}

class Histogram(namespaces: List<String>, name: String) {
    val p99: Metric = namespaces.metric(name, "99pct")
}

class Counter(namespaces: List<String>, name: String) {
    val total: Metric = namespaces.metric(name, "total")
}

class Gauge(namespaces: List<String>, name: String) {
    val value: Metric = namespaces.metric(name)
}

fun Namespace.histogram(name: String = "") = Histogram(this.namespaces, name)
fun Namespace.counter(name: String = "") = Counter(this.namespaces, name)
fun Namespace.gauge(name: String = "") = Gauge(this.namespaces, name)
abstract class Namespace(private val parent: Namespace?, val name: String) {
    val namespaces: List<String>

    init {
        val namespaces = LinkedBlockingDeque<String>()
        namespaces += name
        fun iterateOverParents(parent: Namespace?) {
            if (parent != null) {
                namespaces.addFirst(parent.name)
                iterateOverParents(parent.parent)
            }
        }
        iterateOverParents(parent)
        this.namespaces = namespaces.toList()
    }
}