package io.hamal.app.web3proxy.arbitrum.http

import io.hamal.app.web3proxy.arbitrum.handler.HandleArbitrumRequest
import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotNode
import io.hamal.lib.common.hot.HotObject
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
    fun handle(@RequestBody body: HotNode<*>): ResponseEntity<*> {
        return when (body) {
            is HotArray -> handleArray(body)
            is HotObject -> handleObject(body)
            else -> TODO()
        }
    }

    private fun handleArray(requests: HotArray): ResponseEntity<List<ArbitrumResponse>> {
        val reqs = requests
            .filterIsInstance<HotObject>()
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

    private fun handleObject(request: HotObject): ResponseEntity<ArbitrumResponse> {
        val (err, req) = parseArbitrumRequest(json, request)
        if (err != null) {
            return ResponseEntity.ok(err)
        }
        return ResponseEntity.ok(handle(req!!))
    }
}