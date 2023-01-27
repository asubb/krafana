package krafana.app

import krafana.*
import krafana.Measure.*

fun generalDashboard(dataSource: DataSource) = dashboard {
    title = "General metrics"
    editable = true
    liveNow = true
    refresh = 10.s
    time = (now - 3.h)..now
    with(dataSource) {
        timeseries("Last minute load average on the machine and vCPU count") {
            measure = none
            target {
                expr = metric("plexnode_cpu_vcpus_count")
            }
            val load1min = target {
                expr = metric("plexnode_cpu_load_1min_average")
            }
            expression {
                resample(load1min, 5.m)
                legend("cpu_load_5min_average")
            }
            expression {
                resample(load1min, 15.m)
                legend("cpu_load_15min_average")
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
            val heapUsed = target {
                expr = javaHeapUsedBytes
            }
            expression {
                resample(heapUsed, 5.m)
                legend("java_heap_used_5min_mean", instance)
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
            measure = Measure.pps
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
            measure = Measure.binbps
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
        timeseries("Scheduled tasks") {
            config {
                drawStyle = DrawStyle.Bars
                fillOpacity = 100.0
            }
            target {
                expr = scheduledTriggered.ideltaInterval()
            }
            target {
                expr = scheduledRetried.ideltaInterval()
            }
            target {
                expr = scheduledFailed.ideltaInterval()
            }
        }
        timeseries("Scheduled tasks delay") {
            measure = Measure.ms
            target {
                expr = scheduledTriggeredP95
            }
            target {
                expr = scheduledTriggeredP99
            }
            target {
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
            target {
                expr = feedmasterEnqueued
            }
            target {
                expr = feedmasterDequeued
            }
        }
    }
}