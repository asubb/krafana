package krafana.app

import krafana.*
import krafana.Calcs.*

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
                expr = snapScraped.labelValues(ruuid)
                refresh = TemplateRefresh.OnTimeRangeChanged
                includeAll = true
            }
            template(instanceVar) {
                expr = snapScraped.labelValues(instance)
                refresh = TemplateRefresh.OnTimeRangeChanged
                includeAll = true
            }
        }
        timeseries("Stats collected") {
            gridPos = fullWidth()
            query {
                expr = snapScraped.filter(
                    snapRuuid eq "null",
                    ruuid re ruuidVar,
                    instance re instanceVar
                ).ideltaInterval()
                legend("snaps_scraped", ruuid, path, instance)
            }
        }
        barchart("Running snaps") {
            gridPos = fullWidth()
            options.legend.calcs += last
            options.legend.calcs += max
            val input = query {
                expr = snapStarted
                    .filter(
                        snapRuuid eq "null",
                        ruuid re ruuidVar,
                        instance re instanceVar
                    )
                    .sumBy(ruuid, path, instance)
                hide = true
                legend("Start", ruuid, path, instance)
            }
            val output = query {
                expr = snapFinished
                    .filter(
                        snapRuuid eq "null",
                        ruuid re ruuidVar,
                        instance re instanceVar
                    )
                    .sumBy(ruuid, path, instance)
                hide = true
                legend("Finished", ruuid, path, instance)
            }
            mathExpression(input - output) {
                legend("Active")
            }
        }
        bargauge("Pipelines and their snaps") {
            gridPos = fullWidth()
            reduceOptions(calcs = max)
            query {
                expr = snapStarted.filter(
                    snapRuuid eq "null",
                    ruuid re ruuidVar,
                    instance re instanceVar
                ).sumBy(ruuid, path, instance)
                legend("snaps count", ruuid, path, instance)
            }
        }
        row("Snap CPU") {
            timeseries("Snaps CPU Time") {
                measure = Measure.ns
                query {
                    expr = snapCpuTime.filter(
                        snapRuuid ne "null",
                        ruuid re ruuidVar,
                        instance re instanceVar
                    )
                    legend("snap_cpu_time", snapRuuid, snapName, ruuid, path, instance)
                }
            }
            timeseries("Snaps CPU Wait") {
                measure = Measure.ns
                query {
                    expr = snapCpuWait.filter(
                        snapRuuid ne "null",
                        ruuid re ruuidVar,
                        instance re instanceVar
                    )
                    legend("snap_cpu_wait", snapRuuid, snapName, ruuid, path, instance)
                }
            }
            timeseries("Snaps CPU Block") {
                measure = Measure.ns
                query {
                    expr = snapCpuBlock.filter(
                        snapRuuid ne "null",
                        ruuid re ruuidVar,
                        instance re instanceVar
                    )
                    legend("snap_cpu_block", snapRuuid, snapName, ruuid, path, instance)
                }
            }
        }
        row("Pipeline CPU") {
            val pipelineCpuTime =
                snapCpuTime.filter(snapRuuid eq "null", ruuid re ruuidVar, instance re instanceVar)
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
                        snapRuuid eq "null",
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
                        snapRuuid eq "null",
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
            bargauge("Top pipelines CPU usage by executions") {
                measure = Measure.ns
                colorMode = "continuous-GrYlRd"
                reduceOptions(calcs = mean)
                val usageByPath = query {
                    expr = pipelineCpuTime.deltaInterval().sumBy(ruuid)
                    hide = true
                }
                val countForPath = query {
                    expr = pipelineCpuTime.countBy(ruuid)
                    hide = true
                }
                mathExpression(usageByPath / countForPath) {
                    legend("Pipeline")
                }
            }
        }
        row("Snap Memory") {
            timeseries("Snap ring buffer total size IN") {
                query {
                    expr = snapRingBufferSize.filter(
                        snapRuuid ne "null",
                        ruuid re ruuidVar,
                        instance re instanceVar,
                        direction eq "in"
                    )
                    legend(
                        "snap_buffer_size",
                        snapRuuid,
                        snapName,
                        direction,
                        ruuid,
                        path,
                        viewName
                    )
                }
            }
            timeseries("Snap ring buffer used IN") {
                query {
                    expr = snapRingBufferUsed.filter(
                        snapRuuid ne "null",
                        ruuid re ruuidVar,
                        instance re instanceVar,
                        direction eq "in"
                    )
                    legend(
                        "snap_buffer_size",
                        snapRuuid,
                        snapName,
                        direction,
                        ruuid,
                        path,
                        viewName
                    )
                }
            }
            timeseries("Snap ring buffer used bytes IN") {
                measure = Measure.bytes
                query {
                    expr = snapRingBufferUsedBytes.filter(
                        snapRuuid ne "null",
                        ruuid re ruuidVar,
                        instance re instanceVar,
                        direction eq "in"
                    )
                    legend("snap_buffer_size", snapRuuid, snapName, direction, ruuid, path)
                }
            }
            timeseries("Snap ring buffer total size OUT") {
                query {
                    expr = snapRingBufferSize.filter(
                        snapRuuid ne "null",
                        ruuid re ruuidVar,
                        instance re instanceVar,
                        direction eq "out"
                    )
                    legend("snap_buffer_size", snapRuuid, snapName, direction, ruuid, path)
                }
            }
            timeseries("Snap ring buffer used OUT") {
                query {
                    expr = snapRingBufferUsed.filter(
                        snapRuuid ne "null",
                        ruuid re ruuidVar,
                        instance re instanceVar,
                        direction eq "out"
                    )
                    legend("snap_buffer_size", snapRuuid, snapName, direction, ruuid, path)
                }
            }
            timeseries("Snap ring buffer used bytes OUT") {
                measure = Measure.bytes
                query {
                    expr = snapRingBufferUsedBytes.filter(
                        snapRuuid ne "null",
                        ruuid re ruuidVar,
                        instance re instanceVar,
                        direction eq "out"
                    )
                    legend("snap_buffer_size", snapRuuid, snapName, direction, ruuid, path)
                }
            }
            barchart("Doc average size by path/snap/view") {
                measure = Measure.bytes
                options.legend.calcs += last
                options.legend.calcs += max
                query {
                    expr = metric("plexnode_snap_doc_average_bytes")
                        .filter(
                            ruuid re ruuidVar,
                            instance re instanceVar,
                        )
                        .avgBy(path, snapName, viewName)
                    legend("Doc size", path, snapName, viewName)
                }
            }
            timeseries("Snap in/out docs") {
                query {
                    expr = metric("plexnode_snap_doc_count_total").filter(
                        ruuid re ruuidVar,
                        instance re instanceVar
                    )
                }
            }
            barchart("Snap inflight docs") {
                options.legend.calcs += last
                options.legend.calcs += max
                val input = query {
                    expr = metric("plexnode_snap_doc_count_total").filter(
                        ruuid re ruuidVar,
                        instance re instanceVar,
                        direction eq "in"
                    ).sumBy(snapName, snapRuuid, ruuid)
                    hide = true
                    legend("In", snapName, snapRuuid, ruuid)
                }
                val output = query {
                    expr = metric("plexnode_snap_doc_count_total").filter(
                        ruuid re ruuidVar,
                        instance re instanceVar,
                        direction eq "out"
                    ).sumBy(snapName, snapRuuid, ruuid)
                    hide = true
                    legend("Out", snapName, snapRuuid, ruuid)
                }
                mathExpression(input - output) {
                    legend("Inflight")
                }
            }
        }
        row("Pipeline Memory") {
            timeseries("Pipeline ring buffer bytes overall") {
                measure = Measure.bytes
                query {
                    expr =
                        snapRingBufferUsedBytes.filter(ruuid re ruuidVar, instance re instanceVar)
                            .sumBy(ruuid, path, instance)
                    legend("pipeline_used_bytes", ruuid, path, instance)
                }
            }
            timeseries("CC Heap memory") {
                measure = Measure.bytes
                query {
                    expr = javaHeapUsedBytes.filter(instance re instanceVar)
                }
            }
            timeseries("Distribution of Pipeline Heap Usage projection ${'$'}instance") {
                measure = Measure.bytes
                repeatHorizontal(instance)
                config {
                    drawStyle = DrawStyle.Bars
                    fillOpacity = 100.0
                    stacking = Stacking(mode = StackingMode.Percent)
                }

                pipelineUsedHeap()
            }
            timeseries("Distribution of Pipeline Heap Usage projection on ${'$'}instance") {
                measure = Measure.bytes
                repeatHorizontal(instance)
                config {
                    drawStyle = DrawStyle.Bars
                    fillOpacity = 100.0
                    stacking = Stacking(mode = StackingMode.Normal)
                }

                pipelineUsedHeap()
            }
            bargauge("Top pipelines memory usage by path (average per pipeline)") {
                measure = Measure.bytes
                colorMode = "continuous-GrYlRd"
                reduceOptions(calcs = mean)
                val usageByPath = query {
                    expr = snapRingBufferUsedBytes
                        .filter(
                            ruuid re ruuidVar,
                            instance re instanceVar
                        )
                        .sumBy(path)
                    hide = true
                }
                val countForPath = query {
                    expr = snapScraped
                        .filter(
                            snapRuuid eq "null",
                            ruuid re ruuidVar,
                            instance re instanceVar
                        )
                        .countBy(path)
                    hide = true
                }
                mathExpression(usageByPath / countForPath) {
                    legend("Pipeline")
                }
            }
            bargauge("Top pipelines memory usage by executions") {
                measure = Measure.bytes
                colorMode = "continuous-GrYlRd"
                reduceOptions(calcs = mean)
                val usageByPath = query {
                    expr = snapRingBufferUsedBytes
                        .filter(
                            ruuid re ruuidVar,
                            instance re instanceVar
                        )
                        .sumBy(ruuid)
                    hide = true
                }
                val countForPath = query {
                    expr = snapScraped
                        .filter(
                            snapRuuid eq "null",
                            ruuid re ruuidVar,
                            instance re instanceVar
                        )
                        .countBy(ruuid)
                    hide = true
                }
                mathExpression(usageByPath / countForPath) {
                    legend("Pipeline")
                }
            }
        }
    }
}

private fun Panel<*>.pipelineUsedHeap() {
    val a = query {
        expr = snapRingBufferUsedBytes
            .filter(ruuid re ruuidVar, instance re instanceVar)
            .sumBy(instance)
        hide = true
    }
    val b = query {
        expr = snapRingBufferUsedBytes
            .filter(ruuid re ruuidVar, instance re instanceVar)
            .sumBy(ruuid, path, instance)
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

private fun Panel<*>.pipelineUsedCPU() {
    val allPipelinesConsumption = query {
        expr = snapCpuTime
            .filter(
                snapRuuid eq "null",
                ruuid re ruuidVar,
                instance re instanceVar
            )
            .deltaInterval()
            .sumBy(instance)
        hide = true
    }
    val thePipelineConsumption = query {
        expr = snapCpuTime
            .filter(
                snapRuuid eq "null",
                ruuid re ruuidVar,
                instance re instanceVar
            )
            .deltaInterval()
            .sumBy(ruuid, path, instance)
        hide = true
    }
    val instanceLoad = query {
        expr = cpuProcessLoad
            .filter(instance re instanceVar)
            .sumBy(instance)
        hide = true
    }
    mathExpression(thePipelineConsumption / allPipelinesConsumption * instanceLoad) {
        legend("pipeline_used_cpu", instance, ruuid, path)
    }
}