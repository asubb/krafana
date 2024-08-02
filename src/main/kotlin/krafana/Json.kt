package krafana

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

val json = Json {
    ignoreUnknownKeys = true
    serializersModule = SerializersModule {
        classDiscriminator = "_clazz"
        polymorphic(Panel::class, TimeseriesPanel::class, TimeseriesPanel.serializer())
        polymorphic(Panel::class, RowPanel::class, RowPanel.serializer())
        polymorphic(Panel::class, BargaugePanel::class, BargaugePanel.serializer())
        polymorphic(Panel::class, BarchartPanel::class, BarchartPanel.serializer())
        polymorphic(Panel::class, TablePanel::class, TablePanel.serializer())
    }
    encodeDefaults = true
    explicitNulls = false
}

fun Dashboard.json() = json.encodeToString(this)
