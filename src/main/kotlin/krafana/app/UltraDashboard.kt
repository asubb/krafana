package krafana.app

import krafana.*
import krafana.dashboard

fun ultraDashboard(dataSource: DataSource) = dashboard {
    title = "Ultra"
    editable = true
    liveNow = true
    refresh = 10.s
    time = (now - 15.m)..now
    with(dataSource, tile()) {
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
        timeseries("Ultra Keeper") {
            query {
                expr = ultraAssessment.ideltaInterval().sumBy(instance)
                legend("Assessment", instance)
            }
            query {
                expr =  ultraAssessmentFailed.ideltaInterval().sumBy(instance)
                legend("Assessment failure", instance)
            }
            query {
                expr = ultraAssessmentCalc.ideltaInterval().sumBy(instance)
                legend("Assessment calculation", instance)
            }
            query {
                expr = ultraEnact.ideltaInterval().sumBy(instance)
                legend("Enact", instance)
            }
            query {
                expr = ultraBlocked.ideltaInterval().sumBy(path)
                legend("Blocked by path", path)
            }
        }
        timeseries("Ultra Keeper assessments distribution") {
            query {
                expr = ultraAssessmentNodesP999
            }
            query {
                expr = ultraAssessmentDistr.ideltaInterval().sumBy(instance)
                legend("Assessment distributed by node", instance)
            }
            query {
                expr = ultraAssessmentDistr.ideltaInterval().sum()
                legend("Assessment distributed")
            }
            query {
                expr = ultraAssessmentDistrFailed.ideltaInterval().sumBy(instance)
                legend("Assessment failed to distribute by node", instance)
            }
        }
        timeseries("Ultra Keeper durations") {
            description = "The time taken for assessment on the leader and to enact on the nodes"
            measure = Measure.ms
            query {
                expr = ultraAssessmentP99
            }
            query {
                expr =  ultraAssessmentFailedP99
            }
            query {
                expr =  ultraAssessmentCalcP99
            }
            query {
                expr = ultraAssessmentDistrP99
            }
            query {
                expr = ultraAssessmentDistrFailedP99
            }
            query {
                expr = ultraEnactP99
            }
        }
        timeseries("Ultra prepare/close (success/failure)") {
            query {
                expr = ultraPrepare.ideltaInterval().sumBy(path)
                legend("Prepare by path", path)
            }
            query {
                expr = ultraPrepareFailed.ideltaInterval().sumBy(path)
                legend("Prepare failed by path", path)
            }
            query {
                expr = ultraClose.ideltaInterval().sumBy(path)
                legend("Close by path", path)
            }
            query {
                expr = ultraCloseFailed.ideltaInterval().sumBy(path)
                legend("Close failed by path", path)
            }
        }
        timeseries("Ultra tuples activity") {
            query {
                expr = ultraTuplePrepare.ideltaInterval().sumBy(path)
                legend("Prepare tuple by path", path)
            }
            query {
                expr = ultraTupleClose.ideltaInterval().sumBy(path)
                legend("Close tuple by path", path)
            }
            query {
                expr = ultraTuplePrepareFinished.ideltaInterval().sumBy(path)
                legend("Prepare tuple finished by path", path)
            }
            query {
                expr = ultraTupleCloseFinished.ideltaInterval().sumBy(path)
                legend("Close tuple finished by path", path)
            }
            query {
                expr = ultraTupleScheduled.ideltaInterval().sumBy(path)
                legend("Tuple scheduled by path", path)
            }
            query {
                expr = ultraTupleSkipped.ideltaInterval().sumBy(path)
                legend("Tuple skipped by path", path)
            }
        }
        timeseries("Ultra prepare/close durations (success/failure)") {
            measure = Measure.ms
            query {
                expr = ultraPrepareP99
            }
            query {
                expr = ultraPrepareFailedP99
            }
            query {
                expr = ultraCloseP99
            }
            query {
                expr = ultraCloseFailedP99
            }
            query {
                expr = ultraTuplePrepareFinishedP99
            }
            query {
                expr = ultraTupleCloseFinishedP99
            }
        }
    }
}