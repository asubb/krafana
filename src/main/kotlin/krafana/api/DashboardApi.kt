package krafana.api

import kotlinx.serialization.Serializable
import krafana.Dashboard

class DashboardApi(private val client: GrafanaClient) {

    @Serializable
    data class CreateDashboardRequest(
        val dashboard: Dashboard,
        val overwrite: Boolean,
    )

    suspend fun import(dashboard: Dashboard) {
        client.post("/api/dashboards/db", CreateDashboardRequest(dashboard, overwrite = true))
    }
}