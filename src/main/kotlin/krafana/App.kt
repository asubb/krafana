package krafana

import krafana.Measure.*
import krafana.TemplateRefresh.OnTimeRangeChanged

fun main() {
    val dataSource = DataSource.prometheus("8Hv9_rG4k")

    ultraAutoScaleDashboard(dataSource)
        .json()
        .apply { println("Ultra autoscale dashboard:\n$this") }

    generalDashboard(dataSource)
        .json()
        .apply { println("General dashboard:\n$this") }

    pipelineStatsDashboard(dataSource)
        .json()
        .apply { println("Pipeline stats dashboard:\n$this") }
}

private val feedmasterEnqueued = metric("feedmaster_enqueued")
private val feedmasterDequeued = metric("feedmaster_dequeued")
private val pipelinesActiveTotal = metric("plexnode_pipelines_active_meter_total")
private val autoscale = metric("plexnode_autoscale_count_total")
private val autoscaleDelta = metric("plexnode_autoscale_calc_delta_total")
private val autoscaleScaled = metric("plexnode_autoscale_scaled_total")
private val autoscaleFailed = metric("plexnode_autoscale_calc_failed")
private val plexStateLeader = metric("plexnode_state_leader")
private val plexStateActiveNeighbors = metric("plexnode_state_neighbors_active")
private val snapCpuTime = metric("plexnode_snap_cpu_time_total")
private val snapCpuWait = metric("plexnode_snap_cpu_wait_total")
private val snapCpuBlock = metric("plexnode_snap_cpu_block_total")
private val snapScrapped = metric("plexnode_snap_scraped_total")
private val snapStarted = metric("plexnode_snap_started_total")
private val snapFinished = metric("plexnode_snap_finished_total")
private val snapRingBufferUsed = metric("plexnode_snap_ringbuffer_used_total")
private val snapRingBufferUsedBytes = metric("plexnode_snap_ringbuffer_used_bytes_total")
private val snapRingBufferSize = metric("plexnode_snap_ringbuffer_size")
private val javaHeapUsedBytes = metric("plexnode_java_heap_used_bytes")
private val cpuProcessLoad = metric("plexnode_cpu_process_load_pct")

private val invokerType = label("invoker_type")
private val taskPath = label("taskPath")
private val path = label("path")
private val instance = label("instance")
private val ruuid = label("ruuid")
private val instanceId = label("instanceId")
private val snapName = label("snapName")
private val direction = label("direction")

private val ruuidVar = variable("ruuid")

private fun pipelineStatsDashboard(dataSource: DataSource) = dashboard {
    title = "Pipeline stats"
    editable = true
    liveNow = true
    refresh = 10.s
    time = (now - 15.m)..now
    with(dataSource) {
        templating {
            template(ruuidVar) {
                expr = snapScrapped.labelValues(ruuid)
                refresh = OnTimeRangeChanged
                includeAll = true
            }
        }
        timeseries("Snap stats collected") {
            target {
                expr = snapScrapped.filter(instanceId eq "null", ruuid re ruuidVar)
                legend("snaps_scraped", ruuid, path)
            }
            target {
                expr = snapStarted.filter(instanceId eq "null", ruuid re ruuidVar)
                legend("snaps_started", ruuid, path)
            }
            target {
                expr = snapFinished.filter(instanceId eq "null", ruuid re ruuidVar)
                legend("snaps_finished", ruuid, path)
            }
        }
        timeseries("Snaps CPU Time") {
            measure = ns
            target {
                expr = snapCpuTime.filter(instanceId ne "null", ruuid re ruuidVar)
                legend("snap_cpu_time", instanceId, snapName, ruuid, path)
            }
            target {
                expr = snapCpuTime.filter(instanceId eq "null", ruuid re ruuidVar)
                legend("pipeline_cpu_time", ruuid, path)
            }
        }
        timeseries("Snaps CPU Wait") {
            measure = ns
            target {
                expr = snapCpuWait.filter(instanceId ne "null", ruuid re ruuidVar)
                legend("snap_cpu_wait", instanceId, snapName, ruuid, path)
            }
            target {
                expr = snapCpuWait.filter(instanceId eq "null", ruuid re ruuidVar)
                legend("pipeline_cpu_wait", ruuid, path)
            }
        }
        timeseries("Snaps CPU Block") {
            measure = ns
            target {
                expr = snapCpuBlock.filter(instanceId ne "null", ruuid re ruuidVar)
                legend("snap_cpu_block", instanceId, snapName, ruuid, path)
            }
            target {
                expr = snapCpuBlock.filter(instanceId eq "null", ruuid re ruuidVar)
                legend("pipeline_cpu_block", ruuid, path)
            }
        }
        timeseries("Snap ring buffer") {
            target {
                expr = snapRingBufferSize.filter(instanceId ne "null", ruuid re ruuidVar)
                legend("snap_buffer_size", instanceId, snapName, direction, ruuid, path)
            }
            target {
                expr = snapRingBufferUsed.filter(instanceId ne "null", ruuid re ruuidVar)
                legend("snap_buffer_used", instanceId, snapName, direction, ruuid, path)
            }
        }
        timeseries("Snap ring buffer bytes calculated") {
            measure = bytes
            target {
                expr = snapRingBufferUsedBytes.filter(ruuid re ruuidVar).sumBy(ruuid, path)
                legend("pipeline_used_bytes", ruuid, path)
            }
        }
        timeseries("Doc average size") {
            measure = bytes
            target {
                expr = metric("plexnode_snap_doc_average_bytes").filter(ruuid re ruuidVar)
            }
        }
        timeseries("Snap in/out docs") {
            target {
                expr = metric("plexnode_snap_input_doc_count_total").filter(ruuid re ruuidVar)
            }
            target {
                expr = metric("plexnode_snap_output_doc_count_total").filter(ruuid re ruuidVar)
            }
        }
        timeseries("CC Heap memory") {
            measure = bytes
            target {
                expr = javaHeapUsedBytes
            }
        }
        timeseries("Distribution of Pipeline Heap Usage projection") {
            measure = bytes
            config {
                drawStyle = DrawStyle.Bars
                fillOpacity = 100.0
                stacking = Stacking(mode = StackingMode.Percent)
            }

            val a = target {
                expr = snapRingBufferUsedBytes.sum()
                hide = true
            }
            val b = target {
                expr = snapRingBufferUsedBytes.sumBy(ruuid, path)
                hide = true
            }
            val c = target {
                expr = javaHeapUsedBytes.sum()
                hide = true
            }
            target(DataSource.expression) {
                math(b / a * c)
                legend("pipeline_used_heap")
            }
        }
        timeseries("CC CPU process") {
            measure = percent
            target {
                expr = cpuProcessLoad
            }
        }
        timeseries("Distribution of Pipeline CPU projection") {
            measure = percent
            config {
                drawStyle = DrawStyle.Bars
                fillOpacity = 100.0
                stacking = Stacking(mode = StackingMode.Percent)
            }

            val a = target {
                expr = snapCpuTime.deltaInterval().sum()
                hide = true
            }
            val b = target {
                expr = snapCpuTime.deltaInterval().sumBy(ruuid, path)
                hide = true
            }
            val c = target {
                expr = cpuProcessLoad.sum()
                hide = true
            }
            target(DataSource.expression) {
                math(b / a * c)
                legend("pipeline_used_cpu")
            }
        }
    }
}

private fun ultraAutoScaleDashboard(dataSource: DataSource) = dashboard {
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
            measure = rps
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

private fun generalDashboard(dataSource: DataSource) = dashboard {
    title = "General metrics"
    editable = true
    liveNow = true
    refresh = 10.s
    with(dataSource) {
        timeseries("Last minute load average on the machine and vCPU count") {
            measure = none
            target {
                expr = metric("plexnode_cpu_vcpus_count")
            }
            target {
                expr = metric("plexnode_cpu_load_1min_average")
            }
        }
        timeseries("System/Process CPU Utilization (as % of total)") {
            measure = percent
            target {
                expr = metric("plexnode_cpu_load_pct")
            }
            target {
                expr = cpuProcessLoad
            }
        }
        timeseries("Heap usage in bytes") {
            measure = bytes
            target {
                expr = metric("plexnode_java_heap_total_bytes")
            }
            target {
                expr = javaHeapUsedBytes
            }
        }
        timeseries("Percentage of the heap being used") {
            measure = percent
            target {
                expr = metric("plexnode_java_heap_used_pct")
            }
        }
        timeseries("Disk usage in bytes") {
            measure = bytes
            target {
                expr = metric("plexnode_disk_total_bytes")
            }
            target {
                expr = metric("plexnode_disk_usable_bytes")
            }
            target {
                expr = metric("plexnode_disk_used_bytes")
            }
        }
        timeseries("Percentage usable/used on the disk") {
            measure = percent
            target {
                expr = metric("plexnode_disk_usable_pct")
            }
            target {
                expr = metric("plexnode_disk_used_pct")
            }
        }
        timeseries("Physical memory") {
            measure = bytes
            target {
                expr = metric("plexnode_mem_physical_total_bytes")
            }
            target {
                expr = metric("plexnode_mem_physical_used_bytes")
            }
            target {
                expr = metric("plexnode_mem_physical_free_bytes")
            }
        }
        timeseries("Swap memory") {
            measure = bytes
            target {
                expr = metric("plexnode_mem_swap_total_bytes")
            }
            target {
                expr = metric("plexnode_mem_swap_used_bytes")
            }
            target {
                expr = metric("plexnode_mem_swap_free_bytes")
            }
        }
        timeseries("Virtual committed memory") {
            measure = bytes
            target {
                expr = metric("plexnode_mem_virtual_committed_bytes")
            }
        }
        timeseries("Physical/Swap used percentage") {
            measure = percent
            target {
                expr = metric("plexnode_mem_physical_used_pct")
            }
            target {
                expr = metric("plexnode_mem_swap_used_pct")
            }
        }
        timeseries("Physical/Swap free percentage") {
            measure = percent
            target {
                expr = metric("plexnode_mem_physical_free_pct")
            }
            target {
                expr = metric("plexnode_mem_swap_free_pct")
            }
        }
        timeseries("File Descriptors") {
            measure = none
            target {
                expr = metric("plexnode_file_descriptor_used_count")
            }
            target {
                expr = metric("plexnode_file_descriptor_free_count")
            }
            target {
                expr = metric("plexnode_file_descriptor_max")
            }
        }
        timeseries("File Descriptors used percentage") {
            measure = percent
            target {
                expr = metric("plexnode_file_descriptor_used_pct")
            }
            target {
                expr = metric("plexnode_file_descriptor_free_pct")
            }
        }
        timeseries("Threads/Slots") {
            target {
                expr = metric("plexnode_thread_jvm_count")
            }
            target {
                expr = metric("plexnode_slots_leased")
            }
            target {
                expr = metric("plexnode_slots_max")
            }
        }
        timeseries("Slot leased percentage") {
            measure = percent
            target {
                expr = metric("plexnode_slots_leased_pct")
            }
        }
        timeseries("Slot leased per path") {
            target {
                expr = metric("plexnode_slots_leased_meter_total")
            }
        }
        timeseries("Slot leased per path rate") {
            measure = pps
            target {
                expr = metric("plexnode_slots_leased_meter_oneminrate")
            }
            target {
                expr = metric("plexnode_slots_leased_meter_meanrate")
            }
        }
        timeseries("Network recv/sent bytes") {
            measure = bytes
            target {
                expr = metric("plexnode_net_received_bytes")
            }
            target {
                expr = metric("plexnode_net_sent_bytes")
            }
        }
        timeseries("Network recv/sent/inError/outError/inDrops packet") {
            target {
                expr = metric("plexnode_net_received_packets")
            }
            target {
                expr = metric("plexnode_net_sent_packets")
            }
            target {
                expr = metric("plexnode_net_in_errors")
            }
            target {
                expr = metric("plexnode_net_out_errors")
            }
            target {
                expr = metric("plexnode_net_in_drops")
            }
        }
        timeseries("Network speed") {
            measure = binbps
            target {
                expr = metric("plexnode_net_speed")
            }
        }
        timeseries("Pipelines") {
            target {
                expr = metric("plexnode_pipelines_initiated_meter_total")
            }
            target {
                expr = metric("plexnode_pipelines_finished_meter_total")
            }
            target {
                expr = pipelinesActiveTotal
            }
            target {
                expr = metric("plexnode_pipelines_requested_meter_total")
            }
        }
        timeseries("Feedmaster Broker") {
            target {
                expr = feedmasterEnqueued
            }
            target {
                expr = feedmasterDequeued
            }
        }
    }
    time = (now - 15.m)..now
}