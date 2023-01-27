package krafana.app

import krafana.*
import krafana.dashboard

fun ultraAutoScaleDashboard(dataSource: DataSource) = dashboard {
    title = "Ultra Autoscale"
    editable = true
    liveNow = true
    refresh = 10.s
    time = (now - 15.m)..now
    with(dataSource) {
        timeseries("FM Enqueued-Dequeued") {
            target {
                expr = feedmasterEnqueued
            }
            target {
                expr = feedmasterDequeued
            }
        }
        timeseries("FM Enqueued-Dequeued diff (QueueLength)") {
            target {
                expr = (feedmasterEnqueued - feedmasterDequeued).abs()
                legend("abs(enqueued - dequeued)", taskPath, instance)
            }
        }
        timeseries("FM Enqueued-Dequeued 5 min rate") {
            measure = Measure.rps
            target {
                expr = feedmasterEnqueued.rate(5.m)
                legend("feedmaster_enqueued", taskPath, instance)
            }
            target {
                expr = feedmasterDequeued.rate(5.m)
                legend("feedmaster_dequeued", taskPath, instance)
            }
        }
        timeseries("Active Ultra pipelines") {
            target {
                expr = pipelinesActiveTotal.filter(invokerType eq "ultra")
            }
        }
        timeseries("Scaling engine") {
            target {
                expr = autoscale.changes(30.s)
                legend("success", path, instance)
            }
            target {
                expr = autoscaleFailed.changes(30.s)
                legend("failure", path, instance)
            }
        }
        timeseries("Autoscaling: delta calculated, scaled") {
            target {
                expr = autoscaleDelta.delta(30.s)
                legend("delta", path, instance)
            }
            target {
                expr = autoscaleScaled.delta(30.s)
                legend("scaled", path, instance)
            }
        }
        timeseries("Plex leader") {
            target {
                expr = plexStateLeader
            }
        }
        timeseries("Plex neighbors") {
            target {
                expr = plexStateActiveNeighbors.sumBy(instance)
                legend("active neighbors", instance)
            }
        }
    }
}