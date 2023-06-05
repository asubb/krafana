package krafana.app

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import krafana.api.GrafanaApi
import krafana.json
import java.net.URL

fun main(args: Array<String>) {
    require(args.size == 3) {
        """
        Arguments should provide: 
            1. location of Grafana (e.g. `http://localhost:3000`); 
            2. API token (in Grafana add Service Account via `Configuration` > `Service Accounts` with `Editor` role, and add a new token)
            3. The name of the Prometheus data source to use (e.g. `Prometheus`)
    """.trimIndent()
    }
    val grafanaLocation = URL(args[0])
    val token = args[1]
    val dataSourceName = args[2]

    runBlocking {
        val grafanaApi = GrafanaApi(grafanaLocation, token)

        val dataSource = grafanaApi.dataSource().findByName(dataSourceName)
        println("Found data source: $dataSource")

        listOf(
            ultraDashboard(dataSource),
            generalDashboard(dataSource),
            pipelineStatsDashboard(dataSource),
            apiStatsDashboard(dataSource),
        ).forEach {
            try {
                val dashboard = grafanaApi.dashboard()
                dashboard.import(it)
                println("Dashboard \"${it.title}\" has been imported")
                println("Json:\n${json.encodeToString(it)}")
            } catch (e: Exception) {
                println("Error importing \"${it.title}\"")
                e.printStackTrace()
            }
        }
    }
}
