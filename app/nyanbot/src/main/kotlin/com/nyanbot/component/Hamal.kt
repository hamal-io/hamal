package com.nyanbot.component

import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.sdk.ApiSdkImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class Hamal(
    @Value("\${io.hamal.server.url}") val url: String,
    @Value("\${io.hamal.server.token}") val token: String
) {

    fun setup() {
        val sdk = ApiSdkImpl(HttpTemplateImpl(
            baseUrl = url,
            headerFactory = { this["Authorization"] = "Bearer $token" }
        ))

        val me = sdk.account.me()
        val workspaceId = me.workspaces.first().id

        val namespaces = sdk.namespace.list(workspaceId)
        println(namespaces)
    }

}