package krafana

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class Dashboard(
    var uid: String = Random.nextLong(Long.MAX_VALUE).toString(36),
    var title: String = "New dashboard",
    var tags: MutableList<String> = mutableListOf(),
    var style: Style = Style.default,
    var timezone: Timezone = Timezone.browser,
    var editable: Boolean = false,
    var liveNow: Boolean = true,
    var panels: MutableList<Panel<@Contextual Any>> = mutableListOf(),
    var time: TimeRange = (now - 6.h)..now,
    var refresh: Time? = null,
    var templating: Templating = Templating()
)

fun dashboard(builder: Dashboard.() -> Unit): Dashboard {
    return Dashboard().apply { builder(this) }
}