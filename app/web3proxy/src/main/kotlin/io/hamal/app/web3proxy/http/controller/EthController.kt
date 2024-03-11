package io.hamal.app.web3proxy.http.controller

import io.hamal.app.web3proxy.component.RequestHandler
import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotNode
import io.hamal.lib.common.hot.HotObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class EthController(
    private val ethRequestHandler: RequestHandler
) {

    @PostMapping("/eth")
    fun handle(@RequestBody body: HotNode<*>): ResponseEntity<HotNode<*>> {
        return when (body) {
            is HotArray -> handleArray(body)
            is HotObject -> handleObject(body)
            else -> TODO()
        }
    }

    fun handleArray(@RequestBody body: HotArray): ResponseEntity<HotNode<*>> {
        return ResponseEntity.ok(HotArray.empty)
    }

    fun handleObject(@RequestBody body: HotObject): ResponseEntity<HotNode<*>> {
        return ResponseEntity.ok(HotObject.empty)
    }

}