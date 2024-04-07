package com.nyanbot.http.controller.adhoc

import com.nyanbot.json
import io.hamal.lib.domain._enum.CodeType
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.nodes.Graph
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.lib.sdk.api.ApiAdhocInvokeRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class AdhocInvokeRequest(
    val inputs: InvocationInputs? = null,
    val code: CodeValue
)


@RestController
internal class AdhocController(
    @Value("\${io.hamal.server.url}") val url: String,
    @Value("\${io.hamal.server.token}") val token: String,
) {

    @PostMapping("/v1/adhoc")
    fun invoke(
        @RequestBody req: AdhocInvokeRequest
    ) {
        println("About to send ${req.code.value}")

        val sdk = ApiSdkImpl(HttpTemplateImpl(
            baseUrl = url,
            headerFactory = { this["Authorization"] = "Bearer $token" }
        ))

        sdk.adhoc(
            NamespaceId.root, ApiAdhocInvokeRequest(
                inputs = req.inputs,
                code = CodeValue(
                    json.serialize(
                        Graph(
                            nodes = listOf(),
                            connections = listOf()
                        )
                    )
                ),
                codeType = CodeType.Nodes
            )
        )
    }

}