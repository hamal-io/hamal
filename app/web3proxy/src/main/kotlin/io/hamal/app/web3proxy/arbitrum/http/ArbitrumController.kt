package io.hamal.app.web3proxy.arbitrum.http

import io.hamal.app.web3proxy.arbitrum.handler.HandleArbitrumRequest
import io.hamal.lib.common.serialization.serde.SerdeArray
import io.hamal.lib.common.serialization.serde.SerdeNode
import io.hamal.lib.common.serialization.serde.SerdeObject
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumRequest
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumResponse
import io.hamal.lib.web3.evm.chain.arbitrum.domain.parseArbitrumRequest
import io.hamal.lib.web3.json
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ArbitrumController(
    private val handle: HandleArbitrumRequest
) {

    @PostMapping("/arbitrum")
    fun handle(@RequestBody body: SerdeNode<*>): ResponseEntity<*> {
        return when (body) {
            is SerdeArray -> handleArray(body)
            is SerdeObject -> handleObject(body)
            else -> TODO()
        }
    }

    private fun handleArray(requests: SerdeArray): ResponseEntity<List<ArbitrumResponse>> {
        val reqs = requests
            .filterIsInstance<SerdeObject>()
            .map { request ->
                val (err, req) = parseArbitrumRequest(json, request)
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

    private fun handleObject(request: SerdeObject): ResponseEntity<ArbitrumResponse> {
        val (err, req) = parseArbitrumRequest(json, request)
        if (err != null) {
            return ResponseEntity.ok(err)
        }
        return ResponseEntity.ok(handle(req!!))
    }
}