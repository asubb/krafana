package krafana.app

import krafana.*
import krafana.Measure.*

fun generalDashboard(dataSource: DataSource) = dashboard {
    title = "General metrics"
    editable = true
    liveNow = true
    refresh = 10.s
    time = (now - 3.h)..now
    with(dataSource, tile()) {
        timeseries("Last minute load average on the machine and vCPU count") {
            measure = none
            query {
                expr = metric("plexnode_cpu_vcpus_count")
            }
            val load1min = query {
                expr = metric("plexnode_cpu_load_1min_average")
            }
            resampleExpression(load1min, 5.m) {
                legend("cpu_load_5min_average")
            }
            resampleExpression(load1min, 15.m) {
                legend("cpu_load_15min_average")
            }
        }
        timeseries("System/Process CPU Utilization (as % of total)") {
            measure = percent
            val cpuLoad = query {
                expr = metric("plexnode_cpu_load_pct")
            }
            val cpuProcessLoad = query {
                expr = cpuProcessLoad
            }
            resampleExpression(cpuLoad, 5.m) {
                legend("cpu_load_pct_5min_average")
            }
            resampleExpression(cpuProcessLoad, 5.m) {
                legend("cpu_proces_load_pct_5min_average")
            }
        }
        timeseries("Heap usage in bytes") {
            measure = bytes
            query {
                expr = metric("plexnode_java_heap_total_bytes")
            }
            val heapUsed = query {
                expr = javaHeapUsedBytes
            }
            resampleExpression(heapUsed, 5.m) {
                legend("java_heap_used_5min_mean", instance)
            }
        }
        timeseries("Percentage of the heap being used") {
            measure = percent
            val heapUsed = query {
                expr = metric("plexnode_java_heap_used_pct")
            }
            resampleExpression(heapUsed, 5.m) {
                legend("java_heap_used_pct_5min_mean", instance)
            }
        }
        timeseries("Disk usage in bytes") {
            measure = bytes
            query {
                expr = metric("plexnode_disk_total_bytes")
            }
            query {
                expr = metric("plexnode_disk_usable_bytes")
            }
            query {
                expr = metric("plexnode_disk_used_bytes")
            }
        }
        timeseries("Percentage usable/used on the disk") {
            measure = percent
            query {
                expr = metric("plexnode_disk_usable_pct")
            }
            query {
                expr = metric("plexnode_disk_used_pct")
            }
        }
        timeseries("Physical memory") {
            measure = bytes
            query {
                expr = metric("plexnode_mem_physical_total_bytes")
            }
            query {
                expr = metric("plexnode_mem_physical_used_bytes")
            }
            query {
                expr = metric("plexnode_mem_physical_free_bytes")
            }
        }
        timeseries("Swap memory") {
            measure = bytes
            query {
                expr = metric("plexnode_mem_swap_total_bytes")
            }
            query {
                expr = metric("plexnode_mem_swap_used_bytes")
            }
            query {
                expr = metric("plexnode_mem_swap_free_bytes")
            }
        }
        timeseries("Virtual committed memory") {
            measure = bytes
            query {
                expr = metric("plexnode_mem_virtual_committed_bytes")
            }
        }
        timeseries("Physical/Swap used percentage") {
            measure = percent
            query {
                expr = metric("plexnode_mem_physical_used_pct")
            }
            query {
                expr = metric("plexnode_mem_swap_used_pct")
            }
        }
        timeseries("Physical/Swap free percentage") {
            measure = percent
            query {
                expr = metric("plexnode_mem_physical_free_pct")
            }
            query {
                expr = metric("plexnode_mem_swap_free_pct")
            }
        }
        timeseries("File Descriptors") {
            measure = none
            query {
                expr = metric("plexnode_file_descriptor_used_count")
            }
            query {
                expr = metric("plexnode_file_descriptor_free_count")
            }
            query {
                expr = metric("plexnode_file_descriptor_max")
            }
        }
        timeseries("File Descriptors used percentage") {
            measure = percent
            query {
                expr = metric("plexnode_file_descriptor_used_pct")
            }
            query {
                expr = metric("plexnode_file_descriptor_free_pct")
            }
        }
        timeseries("Threads/Slots") {
            query {
                expr = metric("plexnode_thread_jvm_count")
            }
            query {
                expr = metric("plexnode_slots_leased")
            }
            query {
                expr = metric("plexnode_slots_max")
            }
        }
        timeseries("Slot leased percentage") {
            measure = percent
            query {
                expr = metric("plexnode_slots_leased_pct")
            }
        }
        timeseries("Slot leased per path") {
            query {
                expr = metric("plexnode_slots_leased_meter_total")
            }
        }
        timeseries("Slot leased per path rate") {
            measure = Measure.pps
            query {
                expr = metric("plexnode_slots_leased_meter_oneminrate")
            }
            query {
                expr = metric("plexnode_slots_leased_meter_meanrate")
            }
        }
        timeseries("Network recv/sent bytes") {
            measure = bytes
            query {
                expr = metric("plexnode_net_received_bytes")
            }
            query {
                expr = metric("plexnode_net_sent_bytes")
            }
        }
        timeseries("Network recv/sent/inError/outError/inDrops packet") {
            query {
                expr = metric("plexnode_net_received_packets")
            }
            query {
                expr = metric("plexnode_net_sent_packets")
            }
            query {
                expr = metric("plexnode_net_in_errors")
            }
            query {
                expr = metric("plexnode_net_out_errors")
            }
            query {
                expr = metric("plexnode_net_in_drops")
            }
        }
        timeseries("Network speed") {
            measure = Measure.binbps
            query {
                expr = metric("plexnode_net_speed")
            }
        }
        timeseries("Pipelines") {
            query {
                expr = metric("plexnode_pipelines_initiated_meter_total")
            }
            query {
                expr = metric("plexnode_pipelines_finished_meter_total")
            }
            query {
                expr = pipelinesActiveTotal
            }
            query {
                expr = metric("plexnode_pipelines_requested_meter_total")
            }
        }
        timeseries("Scheduled tasks") {
            config {
                drawStyle = DrawStyle.Bars
                fillOpacity = 100.0
            }
            query {
                expr = scheduledTriggered.ideltaInterval()
            }
            query {
                expr = scheduledRetried.ideltaInterval()
            }
            query {
                expr = scheduledFailed.ideltaInterval()
            }
        }
        timeseries("Scheduled tasks delay") {
            measure = Measure.ms
            query {
                expr = scheduledTriggeredP95
            }
            query {
                expr = scheduledTriggeredP99
            }
            query {
                expr = scheduledTriggeredMax
            }
        }
//        timeseries("Tasks loading") {
//            target {
//                expr = tasksLoaded.ideltaInterval()
//            }
//        }
//        timeseries("Tasks loading duration") {
//            measure = ms
//            target {
//                expr = tasksLoadedP95
//            }
//            target {
//                expr = tasksLoadedMax
//            }
//        }
//        timeseries("Task by path loading") {
//            target {
//                expr = taskLoaded.ideltaInterval()
//            }
//        }
//        timeseries("Task by path loading duration") {
//            measure = ms
//            target {
//                expr = taskLoadedP95
//            }
//            target {
//                expr = taskLoadedMax
//            }
//        }
        timeseries("Feedmaster Broker") {
            query {
                expr = feedmasterEnqueued
            }
            query {
                expr = feedmasterDequeued
            }
        }
    }
}