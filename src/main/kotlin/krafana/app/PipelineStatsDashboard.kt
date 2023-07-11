package krafana.app

import krafana.*
import krafana.ReduceOptionsCalcs.*
import krafana.TargetSettingsMode.dropNN

fun pipelineStatsDashboard(dataSource: DataSource) = dashboard {
    title = "Pipeline stats"
    editable = true
    liveNow = true
    refresh = 10.s
    time = (now - 15.m)..now
    with(dataSource) {
        gridPosSequence = tile()
        templating {
            template(ruuidVar) {
                expr = snapScrapped.labelValues(ruuid)
                refresh = TemplateRefresh.OnTimeRangeChanged
                includeAll = true
            }
        }
        timeseries("Snap stats collected") {
            gridPos = fullWidth()
            query {
                expr = snapScrapped.filter(instanceId eq "null", ruuid re ruuidVar)
                legend("snaps_scraped", ruuid, path, instance)
            }
            query {
                expr = snapStarted.filter(instanceId eq "null", ruuid re ruuidVar)
                legend("snaps_started", ruuid, path, instance)
            }
            query {
                expr = snapFinished.filter(instanceId eq "null", ruuid re ruuidVar)
                legend("snaps_finished", ruuid, path, instance)
            }
        }
        row("Snap CPU") {
            timeseries("Snaps CPU Time") {
                measure = Measure.ns
                query {
                    expr = snapCpuTime.filter(instanceId ne "null", ruuid re ruuidVar)
                    legend("snap_cpu_time", instanceId, snapName, ruuid, path)
                }
            }
            timeseries("Snaps CPU Wait") {
                measure = Measure.ns
                query {
                    expr = snapCpuWait.filter(instanceId ne "null", ruuid re ruuidVar)
                    legend("snap_cpu_wait", instanceId, snapName, ruuid, path)
                }
            }
            timeseries("Snaps CPU Block") {
                measure = Measure.ns
                query {
                    expr = snapCpuBlock.filter(instanceId ne "null", ruuid re ruuidVar)
                    legend("snap_cpu_block", instanceId, snapName, ruuid, path)
                }
            }
        }
        row("Pipeline CPU") {
            val pipelineCpuTime = snapCpuTime.filter(instanceId eq "null", ruuid re ruuidVar)
            timeseries("CPU Time") {
                measure = Measure.ns
                query {
                    expr = pipelineCpuTime
                    legend("pipeline_cpu_time", ruuid, path)
                }
            }
            timeseries("CPU Wait") {
                measure = Measure.ns
                query {
                    expr = snapCpuWait.filter(instanceId eq "null", ruuid re ruuidVar)
                    legend("pipeline_cpu_wait", ruuid, path)
                }
            }
            timeseries("CPU Block") {
                measure = Measure.ns
                query {
                    expr = snapCpuBlock.filter(instanceId eq "null", ruuid re ruuidVar)
                    legend("pipeline_cpu_block", ruuid, path)
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
                pipelineUsedCPU()
            }
            timeseries("Distribution of Pipeline CPU projection") {
                measure = Measure.percent
                config {
                    drawStyle = DrawStyle.Bars
                    fillOpacity = 100.0
                    stacking = Stacking(mode = StackingMode.Normal)
                }
                pipelineUsedCPU()
            }
            timeseries("Active pipelines by Path") {
                config {
                    drawStyle = DrawStyle.Bars
                    fillOpacity = 100.0
                    stacking = Stacking(mode = StackingMode.Normal)
                }

                query {
                    expr = pipelineCpuTime.countBy(path)
                }
            }
            bargauge("Top pipelines CPU usage by path (mean over the period)") {
                measure = Measure.percent
                colorMode = "continuous-GrYlRd"
                reduceOptions(calcs = mean)
                val measuredUsage = query {
                    expr = pipelineCpuTime.deltaInterval().sum()
                    hide = true
                }
                val usageByPath = query {
                    expr = pipelineCpuTime.deltaInterval().sumBy(path)
                    hide = true
                }
                val countForPath = query {
                    expr = pipelineCpuTime.countBy(path)
                    hide = true
                }
                val cpuUsage = query {
                    expr = cpuProcessLoad.sum()
                    hide = true
                }
                val usages = mathExpression(usageByPath / countForPath / measuredUsage * cpuUsage) {
                    hide = true
                }
                reduceExpression(usages, ReducerFunc.mean) {
                    settings.mode = dropNN
                    legend("Pipeline Mean", path)
                }
            }
            bargauge("Top pipelines CPU usage by path (max over the period)") {
                measure = Measure.percent
                colorMode = "continuous-GrYlRd"
                reduceOptions(calcs = mean)
                val measuredUsage = query {
                    expr = pipelineCpuTime.deltaInterval().sum()
                    hide = true
                }
                val usageByPath = query {
                    expr = pipelineCpuTime.deltaInterval().sumBy(path)
                    hide = true
                }
                val countForPath = query {
                    expr = pipelineCpuTime.countBy(path)
                    hide = true
                }
                val cpuUsage = query {
                    expr = cpuProcessLoad.sum()
                    hide = true
                }
                val usages = mathExpression(usageByPath / countForPath / measuredUsage * cpuUsage) {
                    hide = true
                }
                reduceExpression(usages, ReducerFunc.max) {
                    settings.mode = dropNN
                    legend("Pipeline Max", path)
                }
            }
        }
        row("Snap Memory") {
            timeseries("Snap ring buffer") {
                query {
                    expr = snapRingBufferSize.filter(instanceId ne "null", ruuid re ruuidVar)
                    legend("snap_buffer_size", instanceId, snapName, direction, ruuid, path)
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
        }
        row("Pipeline Memory") {
            timeseries("Pipeline ring buffer bytes overall") {
                measure = Measure.bytes
                query {
                    expr = snapRingBufferUsedBytes.filter(ruuid re ruuidVar).sumBy(ruuid, path)
                    legend("pipeline_used_bytes", ruuid, path)
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

                pipelineUsedHeap()
            }
            timeseries("Distribution of Pipeline Heap Usage projection") {
                measure = Measure.bytes
                config {
                    drawStyle = DrawStyle.Bars
                    fillOpacity = 100.0
                    stacking = Stacking(mode = StackingMode.Normal)
                }

                pipelineUsedHeap()
            }
            timeseries("Active pipelines by Path") {
                config {
                    drawStyle = DrawStyle.Bars
                    fillOpacity = 100.0
                    stacking = Stacking(mode = StackingMode.Normal)
                }

                query {
                    expr = snapRingBufferUsedBytes.countBy(path)
                }
            }
            bargauge("Top pipelines memory usage by path (mean over the period)") {
                measure = Measure.bytes
                colorMode = "continuous-GrYlRd"
                reduceOptions(calcs = mean)
                val measuredUsage = query {
                    expr = snapRingBufferUsedBytes.sum()
                    hide = true
                }
                val usageByPath = query {
                    expr = snapRingBufferUsedBytes.sumBy(path)
                    hide = true
                }
                val countForPath = query {
                    expr = snapRingBufferUsedBytes.countBy(path)
                    hide = true
                }
                val javaHeap = query {
                    expr = javaHeapUsedBytes.sum()
                    hide = true
                }
                val usages = mathExpression(usageByPath / countForPath / measuredUsage * javaHeap) {
                    hide = true
                }
                reduceExpression(usages, ReducerFunc.mean) {
                    settings.mode = dropNN
                    legend("Pipeline Mean", path)
                }
            }
            bargauge("Top pipelines memory usage by path (max over the period)") {
                measure = Measure.bytes
                colorMode = "continuous-GrYlRd"
                reduceOptions(calcs = mean)
                val measuredUsage = query {
                    expr = snapRingBufferUsedBytes.sum()
                    hide = true
                }
                val usageByPath = query {
                    expr = snapRingBufferUsedBytes.sumBy(path)
                    hide = true
                }
                val countForPath = query {
                    expr = snapRingBufferUsedBytes.countBy(path)
                    hide = true
                }
                val javaHeap = query {
                    expr = javaHeapUsedBytes.sum()
                    hide = true
                }
                val usages = mathExpression(usageByPath / countForPath / measuredUsage * javaHeap) {
                    hide = true
                }
                reduceExpression(usages, ReducerFunc.max) {
                    settings.mode = dropNN
                    legend("Pipeline Max", path)
                }
            }
        }
    }
}

private fun Panel.pipelineUsedHeap() {
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

private fun Panel.pipelineUsedCPU() {
    val a = query {
        expr = snapCpuTime.filter(instanceId eq "null", ruuid re ruuidVar).deltaInterval().sum()
        hide = true
    }
    val b = query {
        expr = snapCpuTime.filter(instanceId eq "null", ruuid re ruuidVar).deltaInterval().sumBy(ruuid, path)
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