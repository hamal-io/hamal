package io.hamal.app.proxy.web

import io.hamal.app.proxy.handler.EthRequestHandler
import io.hamal.app.proxy.json
import io.hamal.lib.common.hot.HotNode
import io.hamal.lib.common.hot.HotObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class EthRequestController(
    private val ethRequestHandler: EthRequestHandler
) {
    @PostMapping("/")
    fun eth(
        @RequestBody body: HotNode<*>
    ): ResponseEntity<HotNode<*>> {

        when (body) {
//            is HotArray -> {
//                val requests = body.map { json.decodeFromJsonElement(EthRequestHandler.Request.serializer(), it) }
//                val result = ethRequestHandler.handle(requests)
//
//                return ResponseEntity.ok(JsonArray(
//                    result.map {
//                        json.encodeToJsonElement(EthResponse.serializer(), it)
//                    }
//                ))
//            }

            is HotObject -> {
                val result = ethRequestHandler.handle(
                    listOf(json.deserialize(EthRequestHandler.Request::class, json.serialize(body)))
                )

                return ResponseEntity.ok(
                    json.deserialize(HotObject::class, json.serialize(result.first()))
                )
            }

            else -> throw NotImplementedError()
        }
    }
}