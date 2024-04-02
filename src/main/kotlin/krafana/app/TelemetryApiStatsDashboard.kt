package krafana.app

import krafana.*
import krafana.dashboard

fun telemetryApiStatsDashboard(dataSource: DataSource) = dashboard {
    title = "Plex API stats: Telemetry"
    editable = true
    liveNow = true
    refresh = 10.s
    time = (now - 15.m)..now
    with(dataSource, tileOneThird()) {
        templating {
            template(instanceVar) {
                expr = labelValues(instance)
                refresh = TemplateRefresh.OnTimeRangeChanged
                includeAll = true
            }
        }
        row("Queue") {
            timeseries {
                title = "Telemetry queue size"
                query {
                    expr = plexnode.api.telemetry.queue.size.elements.value
                        .filter(instance re instanceVar)
                }
            }
            timeseries {
                measure = Measure.bytes
                title = "Telemetry queue size in bytes"
                query {
                    expr = plexnode.api.telemetry.queue.size.bytes.value
                        .filter(instance re instanceVar)
                }
            }
        }
        row("Successes") {
            timeseries(drawStyle = DrawStyle.Bars) {
                title = "Telemetry request count"
                query {
                    expr = plexnode.api.telemetry.metricPusher.count.total
                        .filter(instance re instanceVar)
                        .ideltaInterval()
                    legend("request_count", instance)
                }
            }
            timeseries(drawStyle = DrawStyle.Bars) {
                title = "Telemetry request duration"
                measure = Measure.ms
                query {
                    expr = plexnode.api.telemetry.metricPusher.duration.p99
                        .filter(instance re instanceVar)
                }
            }
            timeseries(drawStyle = DrawStyle.Bars) {
                title = "Telemetry packet size"
                measure = Measure.bytes
                query {
                    expr = plexnode.api.telemetry.metricPusher.size.p99
                        .filter(instance re instanceVar)
                }
            }
            timeseries(drawStyle = DrawStyle.Bars) {
                title = "Telemetry datapoints per packet"
                query {
                    expr = plexnode.api.telemetry.metricPusher.datapoints.p99
                        .filter(instance re instanceVar)
                }
            }
        }
        row("Failures (statusCode >= 400, exception, dropped)") {
            timeseries(drawStyle = DrawStyle.Bars) {
                title = "Telemetry failures count"
                query {
                    expr = plexnode.api.telemetry.metricPusher.failed.count.total
                        .filter(instance re instanceVar)
                        .ideltaInterval()
                    legend("failures_count", instance)
                }
                query {
                    expr = plexnode.api.telemetry.metricPusher.exception.count.total
                        .filter(instance re instanceVar)
                        .ideltaInterval()
                    legend("exceptions_count", instance)
                }
                query {
                    expr = plexnode.api.telemetry.metric.dropped.count.total
                        .filter(instance re instanceVar)
                        .ideltaInterval()
                    legend("dropped_count", instance)
                }
            }
            timeseries(drawStyle = DrawStyle.Bars) {
                title = "Telemetry failure duration"
                measure = Measure.ms
                val failedCount = query {
                    expr = plexnode.api.telemetry.metricPusher.failed.count.total
                        .filter(instance re instanceVar)
                        .ideltaInterval()
                    hide = true
                }
                val failedDuration = query {
                    expr = plexnode.api.telemetry.metricPusher.failed.duration.p99
                        .filter(instance re instanceVar)
                    hide = true
                }
                mathExpression(failedDuration * failedCount / failedCount) {
                    legend("failures_duration")
                }
                val exceptionCount = query {
                    expr = plexnode.api.telemetry.metricPusher.exception.count.total
                        .filter(instance re instanceVar)
                        .ideltaInterval()
                    hide = true
                }
                val exceptionDuration = query {
                    expr = plexnode.api.telemetry.metricPusher.exception.duration.p99
                        .filter(instance re instanceVar)
                    hide = true
                }
                mathExpression(exceptionDuration * exceptionCount / exceptionCount) {
                    legend("exception_duration")
                }
            }
            timeseries(drawStyle = DrawStyle.Bars) {
                title = "Telemetry failure packet size"
                measure = Measure.bytes
                val failedCount = query {
                    expr = plexnode.api.telemetry.metricPusher.failed.count.total
                        .filter(instance re instanceVar)
                        .ideltaInterval()
                    hide = true
                }
                val failedSize = query {
                    expr = plexnode.api.telemetry.metricPusher.failed.size.p99
                        .filter(instance re instanceVar)
                    hide = true
                }
                mathExpression(failedSize * failedCount / failedCount) {
                    legend("failures_size")
                }
                val exceptionCount = query {
                    expr = plexnode.api.telemetry.metricPusher.exception.count.total
                        .filter(instance re instanceVar)
                        .ideltaInterval()
                    hide = true
                }
                val exceptionSize = query {
                    expr = plexnode.api.telemetry.metricPusher.exception.size.p99
                        .filter(instance re instanceVar)
                    hide = true
                }
                mathExpression(exceptionSize * exceptionCount / exceptionCount) {
                    legend("exception_size")
                }
            }
            timeseries(drawStyle = DrawStyle.Bars) {
                title = "Telemetry failure datapoints per request"
                query {
                    expr = plexnode.api.telemetry.metricPusher.failed.datapoints.p99
                        .filter(instance re instanceVar)
                }
                query {
                    expr = plexnode.api.telemetry.metricPusher.exception.datapoints.p99
                        .filter(instance re instanceVar)
                }
                query {
                    expr = plexnode.api.telemetry.metric.dropped.datapoints.p99
                        .filter(instance re instanceVar)
                }
            }
        }
    }
}