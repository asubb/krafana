package krafana.app

import kotlinx.coroutines.runBlocking
import krafana.api.GrafanaApi
import java.net.URL

fun main() {
    val grafanaLocation = URL("http://localhost:3000")
    val token =
        "eyJrIjoiVjE1MHJxdEZIVG1wUnRTd1BMbENMMzBBbGdtTW1pUzEiLCJuIjoia3JhZmFuYSIsImlkIjoxfQ=="

    runBlocking {
        val grafanaApi = GrafanaApi(grafanaLocation, token)

        val dataSource = grafanaApi.dataSource().findByName("Prometheus")
        println("Found data source: $dataSource")

        ultraAutoScaleDashboard(dataSource)
            .apply { grafanaApi.dashboard().import(this) }

        generalDashboard(dataSource)
            .apply { grafanaApi.dashboard().import(this) }

        pipelineStatsDashboard(dataSource)
            .apply { grafanaApi.dashboard().import(this) }

        apiStatsDashboard(dataSource)
            .apply { grafanaApi.dashboard().import(this) }
    }
}
