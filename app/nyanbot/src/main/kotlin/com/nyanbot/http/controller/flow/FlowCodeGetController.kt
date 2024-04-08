package com.nyanbot.http.controller.flow

import com.nyanbot.repository.FlowId
import com.nyanbot.repository.FlowRepository
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.lib.sdk.api.ApiFunc
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
class FlowCodeGetController(
    private val flowRepository: FlowRepository,
    @Value("\${io.hamal.server.url}") val url: String,
    @Value("\${io.hamal.server.token}") val token: String
) {

    @GetMapping("/v1/flows/{flowId}/code")
    fun invoke(@PathVariable flowId: FlowId): ApiFunc.Code {
        val flow = flowRepository.get(flowId)

        val sdk = ApiSdkImpl(HttpTemplateImpl(
            baseUrl = url,
            headerFactory = { this["Authorization"] = "Bearer $token" }
        ))

        val func = sdk.func.get(flow.funcId!!)

        return func.code
    }

}