package krafana.api

import krafana.Folder

class FolderApi(private val client: GrafanaClient) {
    suspend fun getAll(): List<Folder> {
        return client.get("/api/folders")
    }
}