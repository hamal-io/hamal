package com.nyanbot.http.controller.adhoc

import io.hamal.lib.domain._enum.CodeType
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.lib.sdk.api.ApiAdhocInvokeRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class AdhocInvokeRequest(
    val code: CodeValue
)


@RestController
internal class AdhocController(
    @Value("\${io.hamal.server.url}") val url: String,
    @Value("\${io.hamal.server.token}") val token: String
) {

    @PostMapping("/v1/adhoc")
    fun invoke(
        @RequestBody req: AdhocInvokeRequest
    ) {

        val sdk = ApiSdkImpl(HttpTemplateImpl(
            baseUrl = url,
            headerFactory = { this["Authorization"] = "Bearer $token" }
        ))

        val me = sdk.account.me()
        val workspaceId = me.workspaces.first().id

        // FIXME   change to com::nyanbot:accounts::accountId::adhoc
        sdk.adhoc(
            namespaceId = NamespaceId(workspaceId.value),
            request = ApiAdhocInvokeRequest(
                inputs = InvocationInputs(),
                codeType = CodeType.Nodes,
                code = req.code
            )
        )
    }
}