package krafana.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import krafana.json
import java.net.URL

class GrafanaClient(
    val location: URL,
    val token: String,
) {
    val client = HttpClient() {
        install(ContentNegotiation) {
            json(json)
        }
    }

    suspend inline fun <reified T> get(path: String): T {
        val response = client.get {
            url {
                protocol = URLProtocol.createOrDefault(location.protocol)
                host = location.host
                port = location.port
                path(path.createPath())
            }
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        response.failIfNotSuccess(path)
        return response.body()
    }

    suspend inline fun <reified P> post(path: String, payload: P) {
        val response = client.post {
            url {
                protocol = URLProtocol.createOrDefault(location.protocol)
                host = location.host
                port = location.port
                path(path.createPath())
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        response.failIfNotSuccess(path)
    }

    fun String.createPath() = location.path.removeSuffix("/") + "/" + removePrefix("/")

    suspend fun HttpResponse.failIfNotSuccess(path: String) {
        val req = "${location.protocol}://${location.host}:${location.port}${path.createPath()}"
        require(status == HttpStatusCode.OK) {
            "Got non-200 response for $req: $status: ${
                readBytes().decodeToString()
            }"
        }
    }
}