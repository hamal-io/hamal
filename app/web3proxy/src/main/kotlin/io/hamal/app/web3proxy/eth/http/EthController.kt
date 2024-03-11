package io.hamal.app.web3proxy.eth.http

import io.hamal.app.web3proxy.eth.handler.HandleEthRequest
import io.hamal.app.web3proxy.json
import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotNode
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.web3.eth.domain.EthRequest
import io.hamal.lib.web3.eth.domain.EthResponse
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

    private fun handleArray(@RequestBody requests: HotArray): ResponseEntity<List<EthResponse>> {
        // FIXME json.deserializeList(EthRequest::class, requests)
        val ethRequests = requests.map { obj ->
            json.deserialize(EthRequest::class, json.serialize(obj))
        }
        return ResponseEntity.ok(handle(ethRequests))
    }

    private fun handleObject(@RequestBody request: HotObject): ResponseEntity<EthResponse> {
        val ethRequest = json.deserialize(EthRequest::class, json.serialize(request))
        return ResponseEntity.ok(handle(ethRequest))
    }

}