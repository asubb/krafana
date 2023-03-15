package krafana.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import krafana.Dashboard
import krafana.DataSource
import krafana.Folder
import java.net.URL

class GrafanaApi(location: URL, token: String) {
    private val client = GrafanaClient(location, token)

    fun dataSource() = DataSourceApi(client)
    fun folder() = FolderApi(client)
    fun dashboard() = DashboardApi(client)
}

class GrafanaClient(
    val location: URL,
    val token: String,
) {
    val client = HttpClient() {
        install(ContentNegotiation) {
            json(krafana.json)
        }
    }

    suspend inline fun <reified T> get(path: String): T {
        val response = client.get {
            url {
                protocol = URLProtocol.createOrDefault(location.protocol)
                host = location.host
                port = location.port
                path(path)
            }
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        require(response.status == HttpStatusCode.OK) {
            "Got non-200 response: ${response.status}: ${
                response.readBytes().decodeToString()
            }"
        }
        return response.body()
    }

    suspend inline fun <reified P> post(path: String, payload: P) {
        val response = client.post {
            url {
                protocol = URLProtocol.createOrDefault(location.protocol)
                host = location.host
                port = location.port
                path(path)
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        require(response.status == HttpStatusCode.OK) {
            "Got non-200 response: ${response.status}: ${
                response.readBytes().decodeToString()
            }"
        }
    }
}

class FolderApi(private val client: GrafanaClient) {
    suspend fun getAll(): List<Folder> {
        return client.get("/api/folders")
    }
}

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

class DataSourceApi(private val client: GrafanaClient) {

    suspend fun findByName(name: String): DataSource {
        return client.get("/api/datasources/name/$name")
    }
}