package krafana.app

import krafana.*
import krafana.dashboard

fun apiStatsDashboard(dataSource: DataSource) = dashboard {
    title = "Plex API stats: Telemetry"
    editable = true
    liveNow = true
    refresh = 10.s
    time = (now - 15.m)..now
    with(dataSource) {
        row("Successes") {
            timeseries {
                title = "Telemetry request count"
                query {
                    expr = telemetryMetricPusherCountTotal.ideltaInterval()
                }
            }
            timeseries {
                title = "Telemetry request duration"
                measure = Measure.ms
                query {
                    expr = telemetryMetricPusherDuration99pct
                }
            }
            timeseries {
                title = "Telemetry request size"
                measure = Measure.bytes
                query {
                    expr = telemetryMetricPusherSize99pct
                }
            }
            timeseries {
                title = "Telemetry request size"
                measure = Measure.bytes
                query {
                    expr = telemetryMetricPusherDatapoints99pct
                }
            }
        }
        row("Failures") {
            timeseries {
                title = "Telemetry request count"
                query {
                    expr = telemetryMetricPusherCountFailedTotal.ideltaInterval()
                }
            }
            timeseries {
                title = "Telemetry request exceptions"
                query {
                    expr = telemetryMetricPusherExceptionTotal.ideltaInterval()
                }
            }
            timeseries {
                title = "Telemetry request duration"
                measure = Measure.ms
                query {
                    expr = telemetryMetricPusherFailedDuration99pct
                }
            }
            timeseries {
                title = "Telemetry request size"
                measure = Measure.bytes
                query {
                    expr = telemetryMetricPusherFailedSize99pct
                }
            }
            timeseries {
                title = "Telemetry request size"
                measure = Measure.bytes
                query {
                    expr = telemetryMetricPusherFailedDatapoints99pct
                }
            }
        }
    }
}