package krafana.app

import krafana.*
import krafana.metrics.Namespace
import krafana.metrics.counter
import krafana.metrics.gauge
import krafana.metrics.histogram

object plexnode : Namespace(null, "plexnode") {

    object api : Namespace(plexnode, "api") {

        object telemetry : Namespace(api, "telemetry") {

            object queue : Namespace(telemetry, "queue") {

                object size : Namespace(queue, "size") {
                    val elements = gauge()
                    val bytes = gauge("bytes")
                }
            }

            object metric : Namespace(telemetry, "metric") {

                object dropped : Namespace(metric, "dropped") {
                    val count = counter("")
                    val size = histogram("size")
                    val duration = histogram("duration")
                    val datapoints = histogram("datapoints")
                }
            }

            object metricPusher : Namespace(telemetry, "metric_pusher") {

                object failed : Namespace(metricPusher, "failed") {
                    val count = counter("count")
                    val size = histogram("size")
                    val duration = histogram("duration")
                    val datapoints = histogram("datapoints")
                }

                object exception : Namespace(metricPusher, "exc") {
                    val count = counter("count")
                    val size = histogram("size")
                    val duration = histogram("duration")
                    val datapoints = histogram("datapoints")
                }

                val datapoints = histogram("datapoints")
                val count = counter("count")
                val size = histogram("size")
                val duration = histogram("duration")
            }
        }
    }
}

val ruuidVar = variable("ruuid")
val instanceVar = variable("instance")

val feedmasterEnqueued = metric("feedmaster_enqueued")
val feedmasterDequeued = metric("feedmaster_dequeued")
val pipelinesActiveTotal = metric("plexnode_pipelines_active_total")
val autoscale = metric("plexnode_autoscale_count_total")
val autoscaleDelta = metric("plexnode_autoscale_calc_delta_total")
val autoscaleScaled = metric("plexnode_autoscale_scaled_total")
val autoscaleFailed = metric("plexnode_autoscale_calc_failed")
val plexStateLeader = metric("plexnode_state_leader")
val plexStateActiveNeighbors = metric("plexnode_state_neighbors_active")
val snapCpuTime = metric("plexnode_snap_cpu_time_total")
val snapCpuWait = metric("plexnode_snap_cpu_wait_total")
val snapCpuBlock = metric("plexnode_snap_cpu_block_total")
val snapScraped = metric("plexnode_snap_scraped_total")
val snapStarted = metric("plexnode_snap_started_total")
val snapFinished = metric("plexnode_snap_finished_total")
val snapRingBufferUsed = metric("plexnode_snap_ringbuffer_used")
val snapRingBufferUsedBytes = metric("plexnode_snap_ringbuffer_used_bytes")
val snapRingBufferSize = metric("plexnode_snap_ringbuffer_size")
val javaHeapUsedBytes = metric("plexnode_java_heap_used_bytes")
val javaNonHeapUsedBytes = metric("plexnode_java_nonheap_used_bytes")
val cpuProcessLoad = metric("plexnode_cpu_process_load_pct")
val scheduledTriggeredP95 = metric("plexnode_scheduled_triggered_delay_95pct")
val scheduledTriggeredP99 = metric("plexnode_scheduled_triggered_delay_99pct")
val scheduledTriggeredMax = metric("plexnode_scheduled_triggered_delay_max")
val scheduledTriggered = metric("plexnode_scheduled_triggered_total")
val scheduledRetried = metric("plexnode_scheduled_retried_total")
val scheduledFailed = metric("plexnode_scheduled_failed_total")
val ultraAssessment = metric("ultra_assessment_count")
val ultraAssessmentP99 = metric("ultra_assessment_99pct")
val ultraAssessmentFailed = metric("ultra_assessment_failed_count")
val ultraAssessmentFailedP99 = metric("ultra_assessment_failed_99pct")
val ultraAssessmentNodesP999 = metric("ultra_assessment_nodes_999pct")
val ultraAssessmentCalc = metric("utlra_assessment_calculation_count")
val ultraAssessmentCalcP99 = metric("utlra_assessment_calculation_99pct")
val ultraAssessmentDistr = metric("ultra_assessment_distribution_count")
val ultraAssessmentDistrP99 = metric("ultra_assessment_distribution_99pct")
val ultraAssessmentDistrFailed = metric("ultra_assessment_distribution_failed_count")
val ultraAssessmentDistrFailedP99 = metric("ultra_assessment_distribution_failed_99pct")
val ultraEnact = metric("ultra_enact_count")
val ultraEnactP99 = metric("ultra_enact_99pct")
val ultraPrepare = metric("ultra_prepare_count")
val ultraPrepareP99 = metric("ultra_prepare_99pct")
val ultraPrepareFailed = metric("ultra_prepare_failed_count")
val ultraPrepareFailedP99 = metric("ultra_prepare_failed_99pct")
val ultraClose = metric("ultra_close_count")
val ultraCloseP99 = metric("ultra_close_99pct")
val ultraCloseFailed = metric("ultra_close_failed_count")
val ultraCloseFailedP99 = metric("ultra_close_failed_99pct")
val ultraTuplePrepare = metric("ultra_tuple_prepare_total")
val ultraTuplePrepareFinished = metric("ultra_tuple_prepare_finished_count")
val ultraTuplePrepareFinishedP99 = metric("ultra_tuple_prepare_finished_99pct")
val ultraTupleClose = metric("ultra_tuple_close_total")
val ultraTupleCloseFinished = metric("ultra_tuple_close_finished_count")
val ultraTupleCloseFinishedP99 = metric("ultra_tuple_close_finished_99pct")
val ultraTupleScheduled = metric("ultra_tuple_scheduled_total")
val ultraTupleSkipped = metric("ultra_tuple_skipped_total")
val ultraBlocked = metric("ultra_blocked_total")

val invokerType = label("invoker_type")
val taskPath = label("taskPath")
val path = label("path")
val instance = label("instance")
val ruuid = label("ruuid")
val snapRuuid = label("snapRuuid")
val snapName = label("snapName")
val direction = label("direction")
val viewName = label("viewName")

