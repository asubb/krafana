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
                    expr = telemetryQueueSize
                        .filter(instance re instanceVar)
                }
            }
            timeseries {
                measure = Measure.bytes
                title = "Telemetry queue size in bytes"
                query {
                    expr = telemetryQueueSizeBytes
                        .filter(instance re instanceVar)
                }
            }
        }
        row("Successes") {
            timeseries {
                title = "Telemetry request count"
                query {
                    expr = telemetryMetricPusherCountTotal
                        .filter(instance re instanceVar)
                        .ideltaInterval()
                }
            }
            timeseries {
                title = "Telemetry request duration"
                measure = Measure.ms
                query {
                    expr = telemetryMetricPusherDuration99pct.filter(instance re instanceVar)
                }
            }
            timeseries {
                title = "Telemetry packet size"
                measure = Measure.bytes
                query {
                    expr = telemetryMetricPusherSize99pct.filter(instance re instanceVar)
                }
            }
            timeseries {
                title = "Telemetry datapoints per packet"
                query {
                    expr = telemetryMetricPusherDatapoints99pct.filter(instance re instanceVar)
                }
            }
        }
        row("Failures (statusCode >= 400, exception, dropped)") {
            timeseries {
                title = "Telemetry failures count"
                query {
                    expr = telemetryMetricPusherCountFailedTotal
                        .filter(instance re instanceVar)
                        .ideltaInterval()
                }
                query {
                    expr = telemetryMetricPusherExceptionTotal
                        .filter(instance re instanceVar)
                        .ideltaInterval()
                }
                query {
                    expr = telemetryMetricPusherDroppedTotal
                        .filter(instance re instanceVar)
                        .ideltaInterval()
                }
            }
            timeseries {
                title = "Telemetry failure duration"
                measure = Measure.ms
                query {
                    expr = telemetryMetricPusherFailedDuration99pct.filter(instance re instanceVar)
                }
                query {
                    expr =
                        telemetryMetricPusherExceptionDuration99pct.filter(instance re instanceVar)
                }
                query {
                    expr =
                        telemetryMetricPusherDroppedDuration99pct.filter(instance re instanceVar)
                }
            }
            timeseries {
                title = "Telemetry failure packet size"
                measure = Measure.bytes
                query {
                    expr = telemetryMetricPusherFailedSize99pct.filter(instance re instanceVar)
                }
                query {
                    expr = telemetryMetricPusherExceptionSize99pct.filter(instance re instanceVar)
                }
                query {
                    expr = telemetryMetricPusherDroppedSize99pct.filter(instance re instanceVar)
                }
            }
            timeseries {
                title = "Telemetry failure datapoints per request"
                query {
                    expr =
                        telemetryMetricPusherFailedDatapoints99pct.filter(instance re instanceVar)
                }
                query {
                    expr =
                        telemetryMetricPusherExceptionDatapoints99pct.filter(instance re instanceVar)
                }
                query {
                    expr =
                        telemetryMetricPusherDroppedDatapoints99pct.filter(instance re instanceVar)
                }
            }
        }
    }
}