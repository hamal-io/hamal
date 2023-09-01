package io.hamal.app.proxy.web

import io.hamal.app.proxy.handler.HmlRequestHandler
import io.hamal.lib.web3.hml.domain.HmlResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class HmlRequestController(
    private val json: Json,
    private val hmlRequestHandler: HmlRequestHandler
) {
    @PostMapping("/hml")
    fun hml(
        @RequestBody body: JsonElement
    ): ResponseEntity<JsonElement> {

        when (body) {
            is JsonArray -> {
                val requests = body.map { json.decodeFromJsonElement(HmlRequestHandler.Request.serializer(), it) }
                val result = hmlRequestHandler.handle(requests)

                return ResponseEntity.ok(JsonArray(
                    result.map {
                        json.encodeToJsonElement(HmlResponse.serializer(), it)
                    }
                ))
            }

            is JsonObject -> {
                val result = hmlRequestHandler.handle(
                    listOf(json.decodeFromJsonElement(HmlRequestHandler.Request.serializer(), body))
                )

                return ResponseEntity.ok(json.encodeToJsonElement(HmlResponse.serializer(), result.first()))
            }

            else -> throw NotImplementedError()
        }
    }
}