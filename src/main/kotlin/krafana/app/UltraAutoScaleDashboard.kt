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
            query {
                expr = feedmasterEnqueued
            }
            query {
                expr = feedmasterDequeued
            }
        }
        timeseries("FM Enqueued-Dequeued diff (QueueLength)") {
            query {
                expr = (feedmasterEnqueued - feedmasterDequeued).abs()
                legend("abs(enqueued - dequeued)", taskPath, instance)
            }
        }
        timeseries("FM Enqueued-Dequeued 5 min rate") {
            measure = Measure.rps
            query {
                expr = feedmasterEnqueued.rate(5.m)
                legend("feedmaster_enqueued", taskPath, instance)
            }
            query {
                expr = feedmasterDequeued.rate(5.m)
                legend("feedmaster_dequeued", taskPath, instance)
            }
        }
        timeseries("Active Ultra pipelines") {
            query {
                expr = pipelinesActiveTotal.filter(invokerType eq "ultra")
            }
        }
        timeseries("Scaling engine") {
            query {
                expr = autoscale.changes(30.s)
                legend("success", path, instance)
            }
            query {
                expr = autoscaleFailed.changes(30.s)
                legend("failure", path, instance)
            }
        }
        timeseries("Autoscaling: delta calculated, scaled") {
            query {
                expr = autoscaleDelta.delta(30.s)
                legend("delta", path, instance)
            }
            query {
                expr = autoscaleScaled.delta(30.s)
                legend("scaled", path, instance)
            }
        }
        timeseries("Plex leader") {
            query {
                expr = plexStateLeader
            }
        }
        timeseries("Plex neighbors") {
            query {
                expr = plexStateActiveNeighbors.sumBy(instance)
                legend("active neighbors", instance)
            }
        }
    }
}