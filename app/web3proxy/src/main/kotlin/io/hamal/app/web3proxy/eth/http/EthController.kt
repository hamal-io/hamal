package io.hamal.app.web3proxy.eth.http

import io.hamal.app.web3proxy.eth.handler.HandleEthRequest
import io.hamal.lib.common.serialization.serde.SerdeArray
import io.hamal.lib.common.serialization.serde.SerdeNode
import io.hamal.lib.common.serialization.serde.SerdeObject
import io.hamal.lib.web3.evm.chain.eth.domain.EthRequest
import io.hamal.lib.web3.evm.chain.eth.domain.EthResponse
import io.hamal.lib.web3.evm.chain.eth.domain.parseEthRequest
import io.hamal.lib.web3.json
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class EthController(
    private val handle: HandleEthRequest
) {

    @PostMapping("/eth")
    fun handle(@RequestBody body: SerdeNode<*>): ResponseEntity<*> {
        return when (body) {
            is SerdeArray -> handleArray(body)
            is SerdeObject -> handleObject(body)
            else -> TODO()
        }
    }

    private fun handleArray(requests: SerdeArray): ResponseEntity<List<EthResponse>> {
        val reqs = requests
            .filterIsInstance<SerdeObject>()
            .map { request ->
                val (err, req) = parseEthRequest(json, request)
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

    private fun handleObject(request: SerdeObject): ResponseEntity<EthResponse> {
        val (err, req) = parseEthRequest(json, request)
        if (err != null) {
            return ResponseEntity.ok(err)
        }
        return ResponseEntity.ok(handle(req!!))
    }
}