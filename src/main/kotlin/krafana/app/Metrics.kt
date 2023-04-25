package krafana.app

import krafana.label
import krafana.metric
import krafana.variable

val ruuidVar = variable("ruuid")

const val telemetry = "plexnode_api_telemetry"

val feedmasterEnqueued = metric("feedmaster_enqueued")
val feedmasterDequeued = metric("feedmaster_dequeued")
val pipelinesActiveTotal = metric("plexnode_pipelines_active_meter_total")
val autoscale = metric("plexnode_autoscale_count_total")
val autoscaleDelta = metric("plexnode_autoscale_calc_delta_total")
val autoscaleScaled = metric("plexnode_autoscale_scaled_total")
val autoscaleFailed = metric("plexnode_autoscale_calc_failed")
val plexStateLeader = metric("plexnode_state_leader")
val plexStateActiveNeighbors = metric("plexnode_state_neighbors_active")
val snapCpuTime = metric("plexnode_snap_cpu_time_total")
val snapCpuWait = metric("plexnode_snap_cpu_wait_total")
val snapCpuBlock = metric("plexnode_snap_cpu_block_total")
val snapScrapped = metric("plexnode_snap_scraped_total")
val snapStarted = metric("plexnode_snap_started_total")
val snapFinished = metric("plexnode_snap_finished_total")
val snapRingBufferUsed = metric("plexnode_snap_ringbuffer_used_total")
val snapRingBufferUsedBytes = metric("plexnode_snap_ringbuffer_used_bytes_total")
val snapRingBufferSize = metric("plexnode_snap_ringbuffer_size")
val javaHeapUsedBytes = metric("plexnode_java_heap_used_bytes")
val javaNonHeapUsedBytes = metric("plexnode_java_nonheap_used_bytes")
val cpuProcessLoad = metric("plexnode_cpu_process_load_pct")
val telemetryMetricPusherCountTotal = metric("${telemetry}_metric_pusher_count_total")
val telemetryMetricPusherSize99pct = metric("${telemetry}_metric_pusher_size_99pct")
val telemetryMetricPusherDuration99pct = metric("${telemetry}_metric_pusher_duration_99pct")
val scheduledTriggeredP95 = metric("plexnode_scheduled_triggered_delay_95pct")
val scheduledTriggeredP99 = metric("plexnode_scheduled_triggered_delay_99pct")
val scheduledTriggeredMax = metric("plexnode_scheduled_triggered_delay_max")
val scheduledTriggered = metric("plexnode_scheduled_triggered_total")
val scheduledRetried = metric("plexnode_scheduled_retried_total")
val scheduledFailed = metric("plexnode_scheduled_failed_total")
val tasksLoadedP95 = metric("plexnode_tasks_load_95pct")
val tasksLoadedMax = metric("plexnode_tasks_load__max")
val tasksLoaded = metric("plexnode_tasks_load_count")
val taskLoadedP95 = metric("plexnode_task_load_95pct")
val taskLoadedMax = metric("plexnode_task_load_max")
val taskLoaded = metric("plexnode_task_load_count")


val invokerType = label("invoker_type")
val taskPath = label("taskPath")
val path = label("path")
val instance = label("instance")
val ruuid = label("ruuid")
val instanceId = label("instanceId")
val snapName = label("snapName")
val direction = label("direction")

