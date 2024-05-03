package io.hamal.app.web3proxy.eth.http

import io.hamal.app.web3proxy.eth.handler.HandleEthRequest
import io.hamal.lib.common.serialization.json.JsonArray
import io.hamal.lib.common.serialization.json.JsonNode
import io.hamal.lib.common.serialization.json.JsonObject
import io.hamal.lib.web3.evm.chain.eth.domain.EthRequest
import io.hamal.lib.web3.evm.chain.eth.domain.EthResponse
import io.hamal.lib.web3.evm.chain.eth.domain.parseEthRequest
import io.hamal.lib.web3.serde
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class EthController(
    private val handle: HandleEthRequest
) {

    @PostMapping("/eth")
    fun handle(@RequestBody body: JsonNode<*>): ResponseEntity<*> {
        return when (body) {
            is JsonArray -> handleArray(body)
            is JsonObject -> handleObject(body)
            else -> TODO()
        }
    }

    private fun handleArray(requests: JsonArray): ResponseEntity<List<EthResponse>> {
        val reqs = requests
            .filterIsInstance<JsonObject>()
            .map { request ->
                val (err, req) = parseEthRequest(serde, request)
                if (err != null) {
                    err
                } else {
                    req
                }
            }

        return ResponseEntity.ok(
            handle(reqs.filterIsInstance<EthRequest>())
                .plus(reqs.filterIsInstance<EthResponse>())
        )
    }

    private fun handleObject(request: JsonObject): ResponseEntity<EthResponse> {
        val (err, req) = parseEthRequest(serde, request)
        if (err != null) {
            return ResponseEntity.ok(err)
        }
        return ResponseEntity.ok(handle(req!!))
    }
}