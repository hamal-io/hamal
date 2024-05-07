package io.hamal.app.web3proxy.arbitrum.http

import io.hamal.app.web3proxy.arbitrum.handler.HandleArbitrumRequest
import io.hamal.lib.common.serialization.json.JsonArray
import io.hamal.lib.common.serialization.json.JsonNode
import io.hamal.lib.common.serialization.json.JsonObject
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumRequest
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumResponse
import io.hamal.lib.web3.evm.chain.arbitrum.domain.parseArbitrumRequest
import io.hamal.lib.web3.serde
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ArbitrumController(
    private val handle: HandleArbitrumRequest
) {

    @PostMapping("/arbitrum")
    fun handle(@RequestBody body: JsonNode<*>): ResponseEntity<*> {
        return when (body) {
            is JsonArray -> handleArray(body)
            is JsonObject -> handleObject(body)
            else -> TODO()
        }
    }

    private fun handleArray(requests: JsonArray): ResponseEntity<List<ArbitrumResponse>> {
        val reqs = requests
            .filterIsInstance<JsonObject>()
            .map { request ->
                val (err, req) = parseArbitrumRequest(serde, request)
                if (err != null) {
                    err
                } else {
                    req
                }
            }

        return ResponseEntity.ok(
            handle(reqs.filterIsInstance<ArbitrumRequest>())
                .plus(reqs.filterIsInstance<ArbitrumResponse>())
        )
    }

    private fun handleObject(request: JsonObject): ResponseEntity<ArbitrumResponse> {
        val (err, req) = parseArbitrumRequest(serde, request)
        if (err != null) {
            return ResponseEntity.ok(err)
        }
        return ResponseEntity.ok(handle(req!!))
    }
}