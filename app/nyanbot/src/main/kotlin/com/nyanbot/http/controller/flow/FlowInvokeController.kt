package com.nyanbot.http.controller.flow

import com.nyanbot.repository.FlowId
import com.nyanbot.repository.FlowRepository
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.lib.sdk.api.ApiFuncInvokeRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FlowInvokeController(
    private val flowRepository: FlowRepository,
    @Value("\${io.hamal.server.url}") val url: String,
    @Value("\${io.hamal.server.token}") val token: String
) {

    @PostMapping("/v1/flows/{flowId}/invoke")
    fun invoke(
        @PathVariable flowId: FlowId
    ) {

        // FIXME input must be equal to what ever the trigger returns

        val flow = flowRepository.get(flowId)

        val sdk = ApiSdkImpl(HttpTemplateImpl(
            baseUrl = url,
            headerFactory = { this["Authorization"] = "Bearer $token" }
        ))

        sdk.func.invoke(
            flow.funcId!!, ApiFuncInvokeRequest(
                correlationId = CorrelationId.default,
                inputs = InvocationInputs()
            )
        )


    }
}