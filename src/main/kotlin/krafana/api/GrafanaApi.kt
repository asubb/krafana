package krafana.api

import java.net.URL

class GrafanaApi(location: URL, token: String) {
    private val client = GrafanaClient(location, token)

    fun dataSource() = DataSourceApi(client)
    fun folder() = FolderApi(client)
    fun dashboard() = DashboardApi(client)
}