package krafana

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

private val json = Json {
    serializersModule = SerializersModule {
        classDiscriminator = "_clazz"
        polymorphic(Panel::class, TimeseriesPanel::class, TimeseriesPanel.serializer())
//        polymorphic(Expr::class, PrometheusExpr::class, PrometheusExpr.serializer())
    }
//    prettyPrint = true
    encodeDefaults = true
}

fun Dashboard.json() = json.encodeToString(this)
