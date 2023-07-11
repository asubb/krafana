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
        polymorphic(Panel::class, BarGaugePanel::class, BarGaugePanel.serializer())
//        polymorphic(Expr::class, PrometheusExpr::class, PrometheusExpr.serializer())
    }
//    prettyPrint = true
    encodeDefaults = true
}

fun Dashboard.json() = json.encodeToString(this)
