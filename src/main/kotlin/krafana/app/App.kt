package krafana.app

import krafana.*

fun main() {
    val dataSource = DataSource.prometheus("YBK7ncD4z")

    ultraAutoScaleDashboard(dataSource)
        .json()
        .apply { println("Ultra autoscale dashboard:\n$this") }

    generalDashboard(dataSource)
        .json()
        .apply { println("General dashboard:\n$this") }

    pipelineStatsDashboard(dataSource)
        .json()
        .apply { println("Pipeline stats dashboard:\n$this") }

    apiStatsDashboard(dataSource)
        .json()
        .apply { println("API stats dashboard:\n$this") }
}
