package krafana.app

import krafana.*
import krafana.ReduceOptionsCalcs.*

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
            template(instanceVar) {
                expr = snapScrapped.labelValues(instance)
                refresh = TemplateRefresh.OnTimeRangeChanged
                includeAll = true
            }
        }
        timeseries("Snap stats collected") {
            gridPos = fullWidth()
            query {
                expr = snapScrapped.filter(
                    instanceId eq "null",
                    ruuid re ruuidVar,
                    instance re instanceVar
                )
                legend("snaps_scraped", ruuid, path, instance)
            }
            query {
                expr = snapStarted.filter(
                    instanceId eq "null",
                    ruuid re ruuidVar,
                    instance re instanceVar
                )
                legend("snaps_started", ruuid, path, instance)
            }
            query {
                expr = snapFinished.filter(
                    instanceId eq "null",
                    ruuid re ruuidVar,
                    instance re instanceVar
                )
                legend("snaps_finished", ruuid, path, instance)
            }
        }
        row("Snap CPU") {
            timeseries("Snaps CPU Time") {
                measure = Measure.ns
                query {
                    expr = snapCpuTime.filter(
                        instanceId ne "null",
                        ruuid re ruuidVar,
                        instance re instanceVar
                    )
                    legend("snap_cpu_time", instanceId, snapName, ruuid, path, instance)
                }
            }
            timeseries("Snaps CPU Wait") {
                measure = Measure.ns
                query {
                    expr = snapCpuWait.filter(
                        instanceId ne "null",
                        ruuid re ruuidVar,
                        instance re instanceVar
                    )
                    legend("snap_cpu_wait", instanceId, snapName, ruuid, path, instance)
                }
            }
            timeseries("Snaps CPU Block") {
                measure = Measure.ns
                query {
                    expr = snapCpuBlock.filter(
                        instanceId ne "null",
                        ruuid re ruuidVar,
                        instance re instanceVar
                    )
                    legend("snap_cpu_block", instanceId, snapName, ruuid, path, instance)
                }
            }
        }
        row("Pipeline CPU") {
            val pipelineCpuTime =
                snapCpuTime.filter(instanceId eq "null", ruuid re ruuidVar, instance re instanceVar)
            timeseries("CPU Time") {
                measure = Measure.ns
                query {
                    expr = pipelineCpuTime
                    legend("pipeline_cpu_time", ruuid, path, instance)
                }
            }
            timeseries("CPU Wait") {
                measure = Measure.ns
                query {
                    expr = snapCpuWait.filter(
                        instanceId eq "null",
                        ruuid re ruuidVar,
                        instance re instanceVar
                    )
                    legend("pipeline_cpu_wait", ruuid, path, instance)
                }
            }
            timeseries("CPU Block") {
                measure = Measure.ns
                query {
                    expr = snapCpuBlock.filter(
                        instanceId eq "null",
                        ruuid re ruuidVar,
                        instance re instanceVar
                    )
                    legend("pipeline_cpu_block", ruuid, path, instance)
                }
            }
            timeseries("CC CPU process") {
                measure = Measure.percent
                query {
                    expr = cpuProcessLoad.filter(instance re instanceVar)
                }
            }
            timeseries("Distribution of Pipeline CPU projection (in comparison) on ${'$'}instance") {
                measure = Measure.percent
                repeatHorizontal(instance)
                config {
                    drawStyle = DrawStyle.Bars
                    fillOpacity = 100.0
                    stacking = Stacking(mode = StackingMode.Percent)
                }
                pipelineUsedCPU()
            }
            timeseries("Distribution of Pipeline CPU projection on ${'$'}instance") {
                measure = Measure.percent
                repeatHorizontal(instance)
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
            bargauge("Top pipelines CPU usage by path (average time for execution)") {
                measure = Measure.ns
                colorMode = "continuous-GrYlRd"
                reduceOptions(calcs = mean)
                val usageByPath = query {
                    expr = pipelineCpuTime.deltaInterval().sumBy(path)
                    hide = true
                }
                val countForPath = query {
                    expr = pipelineCpuTime.countBy(path)
                    hide = true
                }
                mathExpression(usageByPath / countForPath) {
                    legend("Pipeline")
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
            timeseries("Distribution of Pipeline Heap Usage projection ${'$'}instance") {
                measure = Measure.bytes
                repeatVertical(instance)
                config {
                    drawStyle = DrawStyle.Bars
                    fillOpacity = 100.0
                    stacking = Stacking(mode = StackingMode.Percent)
                }

                pipelineUsedHeap()
            }
            timeseries("Distribution of Pipeline Heap Usage projection on ${'$'}instance") {
                measure = Measure.bytes
                repeatVertical(instance)
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
                val usageByPath = query {
                    expr = snapRingBufferUsedBytes.sumBy(path)
                    hide = true
                }
                val countForPath = query {
                    expr = snapRingBufferUsedBytes.countBy(path)
                    hide = true
                }
                mathExpression(usageByPath / countForPath) {
                    legend("Pipeline")
                }
            }
        }
    }
}

private fun Panel.pipelineUsedHeap() {
    val a = query {
        expr = snapRingBufferUsedBytes.filter(instance re instanceVar).sumBy(instance)
        hide = true
    }
    val b = query {
        expr = snapRingBufferUsedBytes.filter(instance re instanceVar).sumBy(ruuid, path, instance)
        hide = true
    }
    val c = query {
        expr = javaHeapUsedBytes.filter(instance re instanceVar).sumBy(instance)
        hide = true
    }
    mathExpression(b / a * c) {
        legend("pipeline_used_heap")
    }
}

private fun Panel.pipelineUsedCPU() {
    val a = query {
        expr = snapCpuTime.filter(
            instanceId eq "null",
            ruuid re ruuidVar,
            instance re instanceVar
        )
            .deltaInterval()
            .sumBy(instance)
        hide = true
    }
    val b = query {
        expr = snapCpuTime.filter(instanceId eq "null", ruuid re ruuidVar, instance re instanceVar)
            .deltaInterval().sumBy(ruuid, path, instance)
        hide = true
    }
    val c = query {
        expr = cpuProcessLoad.filter(instance re instanceVar).sumBy(instance)
        hide = true
    }
    mathExpression(b / a * c) {
        legend("pipeline_used_cpu", instance, ruuid, path)
    }
}