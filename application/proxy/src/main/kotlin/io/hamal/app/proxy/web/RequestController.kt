package io.hamal.app.proxy.web

import io.hamal.app.proxy.handler.EthRequestHandler
import io.hamal.lib.web3.eth.domain.EthResp
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RequestController(
    private val json: Json,
    private val requestHandler: EthRequestHandler
) {
    @PostMapping("/")
    fun eth(
        @RequestBody body: JsonElement
    ): ResponseEntity<JsonElement> {

        when (body) {
            is JsonArray -> {
                val requests = body.map { json.decodeFromJsonElement(EthRequestHandler.Request.serializer(), it) }
                val result = requestHandler.handle(requests)

                return ResponseEntity.ok(JsonArray(
                    result.map {
                        json.encodeToJsonElement(EthResp.serializer(), it)
                    }
                ))
            }

            is JsonObject -> {
                val result = requestHandler.handle(
                    listOf(json.decodeFromJsonElement(EthRequestHandler.Request.serializer(), body))
                )

                return ResponseEntity.ok(json.encodeToJsonElement(EthResp.serializer(), result.first()))
            }

            else -> throw NotImplementedError()
        }
    }
}