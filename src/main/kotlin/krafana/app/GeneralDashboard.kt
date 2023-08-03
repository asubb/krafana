package krafana.app

import krafana.*
import krafana.Measure.*

fun generalDashboard(dataSource: DataSource) = dashboard {
    title = "General metrics"
    editable = true
    liveNow = true
    refresh = 10.s
    time = (now - 15.m)..now
    with(dataSource, tile()) {
        templating {
            template(instanceVar) {
                expr = cpuProcessLoad.labelValues(instance)
                refresh = TemplateRefresh.OnTimeRangeChanged
                includeAll = true
            }
        }
        timeseries("Last minute load average on the machine and vCPU count") {
            query {
                expr = metric("plexnode_cpu_vcpus_count").filter(instance re instanceVar)
            }
            val load1min = query {
                expr = metric("plexnode_cpu_load_1min_average").filter(instance re instanceVar)
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
                expr = metric("plexnode_cpu_load_pct").filter(instance re instanceVar)
            }
            val cpuProcessLoad = query {
                expr = cpuProcessLoad.filter(instance re instanceVar)
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
                expr = metric("plexnode_java_heap_total_bytes").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_java_heap_max_bytes").filter(instance re instanceVar)
            }
            val heapUsed = query {
                expr = javaHeapUsedBytes.filter(instance re instanceVar)
            }
            resampleExpression(heapUsed, 5.m) {
                legend("java_heap_used_5min_mean", instance)
            }
        }
        timeseries("Percentage of the heap being used (of total and max)") {
            measure = percent
            val heapUsed = query {
                expr = metric("plexnode_java_heap_used_pct").filter(instance re instanceVar)
            }
            resampleExpression(heapUsed, 5.m) {
                legend("java_heap_used_pct_5min_mean", instance)
            }
            val heapUsedOfMax = query {
                expr = metric("plexnode_java_heap_used_of_max_pct").filter(instance re instanceVar)
            }
            resampleExpression(heapUsedOfMax, 5.m) {
                legend("java_heap_used_of_max_pct_5min_mean", instance)
            }
        }
        timeseries("Non-heap usage in bytes") {
            measure = bytes
            query {
                expr = metric("plexnode_java_nonheap_total_bytes").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_java_nonheap_max_bytes").filter(instance re instanceVar)
            }
            val nonheapUsed = query {
                expr = javaNonHeapUsedBytes.filter(instance re instanceVar)
            }
            resampleExpression(nonheapUsed, 5.m) {
                legend("java_nonheap_used_5min_mean", instance)
            }
        }
        timeseries("Percentage of the non-heap being used (of total and max)") {
            measure = percent
            val nonheapUsed = query {
                expr = metric("plexnode_java_non_heap_used_pct").filter(instance re instanceVar)
            }
            resampleExpression(nonheapUsed, 5.m) {
                legend("java_nonheap_used_pct_5min_mean", instance)
            }
            val nonheapUsedOfMax = query {
                expr = metric("plexnode_java_nonheap_used_of_max_pct").filter(instance re instanceVar)
            }
            resampleExpression(nonheapUsedOfMax, 5.m) {
                legend("java_heap_used_of_max_pct_5min_mean", instance)
            }
        }
        timeseries("Disk usage in bytes") {
            measure = bytes
            query {
                expr = metric("plexnode_disk_total_bytes").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_disk_usable_bytes").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_disk_used_bytes").filter(instance re instanceVar)
            }
        }
        timeseries("Percentage usable/used on the disk") {
            measure = percent
            query {
                expr = metric("plexnode_disk_usable_pct").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_disk_used_pct").filter(instance re instanceVar)
            }
        }
        timeseries("Physical memory") {
            measure = bytes
            query {
                expr = metric("plexnode_mem_physical_total_bytes").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_mem_physical_used_bytes").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_mem_physical_free_bytes").filter(instance re instanceVar)
            }
        }
        timeseries("Swap memory") {
            measure = bytes
            query {
                expr = metric("plexnode_mem_swap_total_bytes").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_mem_swap_used_bytes").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_mem_swap_free_bytes").filter(instance re instanceVar)
            }
        }
        timeseries("Virtual committed memory") {
            measure = bytes
            query {
                expr = metric("plexnode_mem_virtual_committed_bytes").filter(instance re instanceVar)
            }
        }
        timeseries("Physical/Swap used percentage") {
            measure = percent
            query {
                expr = metric("plexnode_mem_physical_used_pct").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_mem_swap_used_pct").filter(instance re instanceVar)
            }
        }
        timeseries("Physical/Swap free percentage") {
            measure = percent
            query {
                expr = metric("plexnode_mem_physical_free_pct").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_mem_swap_free_pct").filter(instance re instanceVar)
            }
        }
        timeseries("File Descriptors") {
            measure = none
            query {
                expr = metric("plexnode_file_descriptor_used_count").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_file_descriptor_free_count").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_file_descriptor_max").filter(instance re instanceVar)
            }
        }
        timeseries("File Descriptors used percentage") {
            measure = percent
            query {
                expr = metric("plexnode_file_descriptor_used_pct").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_file_descriptor_free_pct").filter(instance re instanceVar)
            }
        }
        timeseries("Threads/Slots") {
            query {
                expr = metric("plexnode_thread_jvm_count").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_slots_leased").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_slots_max").filter(instance re instanceVar)
            }
        }
        timeseries("Slot leased percentage") {
            measure = percent
            query {
                expr = metric("plexnode_slots_leased_pct").filter(instance re instanceVar)
            }
        }
        timeseries("Slot leased per path") {
            query {
                expr = metric("plexnode_slots_leased_meter_total").filter(instance re instanceVar)
            }
        }
        timeseries("Slot leased per path rate") {
            measure = Measure.pps
            query {
                expr = metric("plexnode_slots_leased_meter_oneminrate").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_slots_leased_meter_meanrate").filter(instance re instanceVar)
            }
        }
        timeseries("Network recv/sent bytes") {
            measure = bytes
            query {
                expr = metric("plexnode_net_received_bytes").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_net_sent_bytes").filter(instance re instanceVar)
            }
        }
        timeseries("Network recv/sent/inError/outError/inDrops packet") {
            query {
                expr = metric("plexnode_net_received_packets").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_net_sent_packets").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_net_in_errors").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_net_out_errors").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_net_in_drops").filter(instance re instanceVar)
            }
        }
        timeseries("Network speed") {
            measure = Measure.binbps
            query {
                expr = metric("plexnode_net_speed").filter(instance re instanceVar)
            }
        }
        timeseries("Pipelines") {
            query {
                expr = metric("plexnode_pipelines_initiated_meter_total").filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_pipelines_finished_meter_total").filter(instance re instanceVar)
            }
            query {
                expr = pipelinesActiveTotal.filter(instance re instanceVar)
            }
            query {
                expr = metric("plexnode_pipelines_requested_meter_total").filter(instance re instanceVar)
            }
        }
        timeseries("Scheduled tasks") {
            config {
                drawStyle = DrawStyle.Bars
                fillOpacity = 100.0
            }
            query {
                expr = scheduledTriggered.filter(instance re instanceVar).ideltaInterval()
            }
            query {
                expr = scheduledRetried.filter(instance re instanceVar).ideltaInterval()
            }
            query {
                expr = scheduledFailed.filter(instance re instanceVar).ideltaInterval()
            }
        }
        timeseries("Scheduled tasks delay") {
            measure = Measure.ms
            query {
                expr = scheduledTriggeredP95.filter(instance re instanceVar)
            }
            query {
                expr = scheduledTriggeredP99.filter(instance re instanceVar)
            }
            query {
                expr = scheduledTriggeredMax.filter(instance re instanceVar)
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
                expr = feedmasterEnqueued.filter(instance re instanceVar)
            }
            query {
                expr = feedmasterDequeued.filter(instance re instanceVar)
            }
        }
    }
}