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
        timeseries {
            title = "Telemetry request count"
            query {
                expr = telemetryMetricPusherCountTotal
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
    }
}