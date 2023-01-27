package krafana.app

import krafana.*

fun pipelineStatsDashboard(dataSource: DataSource) = dashboard {
    title = "Pipeline stats"
    editable = true
    liveNow = true
    refresh = 10.s
    time = (now - 15.m)..now
    with(dataSource) {
        templating {
            template(ruuidVar) {
                expr = snapScrapped.labelValues(ruuid)
                refresh = TemplateRefresh.OnTimeRangeChanged
                includeAll = true
            }
        }
        timeseries("Snap stats collected") {
            query {
                expr = snapScrapped.filter(instanceId eq "null", ruuid re ruuidVar)
                legend("snaps_scraped", ruuid, path)
            }
            query {
                expr = snapStarted.filter(instanceId eq "null", ruuid re ruuidVar)
                legend("snaps_started", ruuid, path)
            }
            query {
                expr = snapFinished.filter(instanceId eq "null", ruuid re ruuidVar)
                legend("snaps_finished", ruuid, path)
            }
        }
        timeseries("Snaps CPU Time") {
            measure = Measure.ns
            query {
                expr = snapCpuTime.filter(instanceId ne "null", ruuid re ruuidVar)
                legend("snap_cpu_time", instanceId, snapName, ruuid, path)
            }
            query {
                expr = snapCpuTime.filter(instanceId eq "null", ruuid re ruuidVar)
                legend("pipeline_cpu_time", ruuid, path)
            }
        }
        timeseries("Snaps CPU Wait") {
            measure = Measure.ns
            query {
                expr = snapCpuWait.filter(instanceId ne "null", ruuid re ruuidVar)
                legend("snap_cpu_wait", instanceId, snapName, ruuid, path)
            }
            query {
                expr = snapCpuWait.filter(instanceId eq "null", ruuid re ruuidVar)
                legend("pipeline_cpu_wait", ruuid, path)
            }
        }
        timeseries("Snaps CPU Block") {
            measure = Measure.ns
            query {
                expr = snapCpuBlock.filter(instanceId ne "null", ruuid re ruuidVar)
                legend("snap_cpu_block", instanceId, snapName, ruuid, path)
            }
            query {
                expr = snapCpuBlock.filter(instanceId eq "null", ruuid re ruuidVar)
                legend("pipeline_cpu_block", ruuid, path)
            }
        }
        timeseries("Snap ring buffer") {
            query {
                expr = snapRingBufferSize.filter(instanceId ne "null", ruuid re ruuidVar)
                legend("snap_buffer_size", instanceId, snapName, direction, ruuid, path)
            }
            query {
                expr = snapRingBufferUsed.filter(instanceId ne "null", ruuid re ruuidVar)
                legend("snap_buffer_used", instanceId, snapName, direction, ruuid, path)
            }
        }
        timeseries("Snap ring buffer bytes calculated") {
            measure = Measure.bytes
            query {
                expr = snapRingBufferUsedBytes.filter(ruuid re ruuidVar).sumBy(ruuid, path)
                legend("pipeline_used_bytes", ruuid, path)
            }
        }
        timeseries("Doc average size") {
            measure = Measure.bytes
            query {
                expr = metric("plexnode_snap_doc_average_bytes").filter(ruuid re ruuidVar)
            }
        }
        timeseries("Snap in/out docs") {
            query {
                expr = metric("plexnode_snap_input_doc_count_total").filter(ruuid re ruuidVar)
            }
            query {
                expr = metric("plexnode_snap_output_doc_count_total").filter(ruuid re ruuidVar)
            }
        }
        timeseries("CC Heap memory") {
            measure = Measure.bytes
            query {
                expr = javaHeapUsedBytes
            }
        }
        timeseries("Distribution of Pipeline Heap Usage projection") {
            measure = Measure.bytes
            config {
                drawStyle = DrawStyle.Bars
                fillOpacity = 100.0
                stacking = Stacking(mode = StackingMode.Percent)
            }

            val a = query {
                expr = snapRingBufferUsedBytes.sum()
                hide = true
            }
            val b = query {
                expr = snapRingBufferUsedBytes.sumBy(ruuid, path)
                hide = true
            }
            val c = query {
                expr = javaHeapUsedBytes.sum()
                hide = true
            }
            mathExpression(b / a * c) {
                legend("pipeline_used_heap")
            }
        }
        timeseries("CC CPU process") {
            measure = Measure.percent
            query {
                expr = cpuProcessLoad
            }
        }
        timeseries("Distribution of Pipeline CPU projection") {
            measure = Measure.percent
            config {
                drawStyle = DrawStyle.Bars
                fillOpacity = 100.0
                stacking = Stacking(mode = StackingMode.Percent)
            }
            val a = query {
                expr = snapCpuTime.deltaInterval().sum()
                hide = true
            }
            val b = query {
                expr = snapCpuTime.deltaInterval().sumBy(ruuid, path)
                hide = true
            }
            val c = query {
                expr = cpuProcessLoad.sum()
                hide = true
            }
            mathExpression(b / a * c) {
                legend("pipeline_used_cpu")
            }
        }
    }
}