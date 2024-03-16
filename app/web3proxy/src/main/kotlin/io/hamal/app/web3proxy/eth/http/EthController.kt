package io.hamal.app.web3proxy.eth.http

import io.hamal.app.web3proxy.eth.handler.HandleEthRequest
import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotNode
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.web3.eth.domain.EthRequest
import io.hamal.lib.web3.eth.domain.EthResponse
import io.hamal.lib.web3.eth.domain.parseRequest
import io.hamal.lib.web3.eth.json
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class EthController(
    private val handle: HandleEthRequest
) {

    @PostMapping("/eth")
    fun handle(@RequestBody body: HotNode<*>): ResponseEntity<*> {
        return when (body) {
            is HotArray -> handleArray(body)
            is HotObject -> handleObject(body)
            else -> TODO()
        }
    }

    private fun handleArray(requests: HotArray): ResponseEntity<List<EthResponse>> {
        val reqs = requests
            .filterIsInstance<HotObject>()
            .map { request ->
                val (err, req) = parseRequest(json, request)
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

    private fun handleObject(request: HotObject): ResponseEntity<EthResponse> {
        val (err, req) = parseRequest(json, request)
        if (err != null) {
            return ResponseEntity.ok(err)
        }
        return ResponseEntity.ok(handle(req!!))
    }
}