package krafana.api

import krafana.DataSource

class DataSourceApi(private val client: GrafanaClient) {

    suspend fun findByName(name: String): DataSource {
        return client.get("/api/datasources/name/$name")
    }
}