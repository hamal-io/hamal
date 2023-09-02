package io.hamal.app.proxy.web

import io.hamal.app.proxy.handler.EthRequestHandler
import io.hamal.lib.web3.eth.domain.EthResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class EthRequestController(
    private val json: Json,
    private val ethRequestHandler: EthRequestHandler
) {
    @PostMapping("/")
    fun eth(
        @RequestBody body: JsonElement
    ): ResponseEntity<JsonElement> {

        when (body) {
            is JsonArray -> {
                val requests = body.map { json.decodeFromJsonElement(EthRequestHandler.Request.serializer(), it) }
                val result = ethRequestHandler.handle(requests)

                return ResponseEntity.ok(JsonArray(
                    result.map {
                        json.encodeToJsonElement(EthResponse.serializer(), it)
                    }
                ))
            }

            is JsonObject -> {
                val result = ethRequestHandler.handle(
                    listOf(json.decodeFromJsonElement(EthRequestHandler.Request.serializer(), body))
                )

                return ResponseEntity.ok(json.encodeToJsonElement(EthResponse.serializer(), result.first()))
            }

            else -> throw NotImplementedError()
        }
    }
}