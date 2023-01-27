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
            target {
                expr = telemetryMetricPusherCountTotal
            }
        }
        timeseries {
            title = "Telemetry request duration"
            measure = Measure.ms
            target {
                expr = telemetryMetricPusherDuration99pct
            }
        }
        timeseries {
            title = "Telemetry request size"
            measure = Measure.bytes
            target {
                expr = telemetryMetricPusherSize99pct
            }
        }
    }
}