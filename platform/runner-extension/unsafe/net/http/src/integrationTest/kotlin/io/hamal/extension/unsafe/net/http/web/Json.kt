package io.hamal.extension.unsafe.net.http.web

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
open class TestJsonController {

    @RequestMapping("/v1/json-empty-object")
    fun jsonEmptyObject(): ResponseEntity<JsonObject> {
        return ResponseEntity.ok(JsonObject(mapOf()))
    }

    @RequestMapping("/v1/json-empty-array")
    fun jsonEmptyArray(): ResponseEntity<JsonArray> {
        return ResponseEntity.ok(JsonArray(listOf()))
    }

    @RequestMapping("/v1/json-error")
    fun jsonError(): ResponseEntity<JsonObject> {
        return ResponseEntity.badRequest().body(
            JsonObject(
                mapOf(
                    "code" to JsonPrimitive(400),
                    "message" to JsonPrimitive("bad-request"),
                    "boolean-value" to JsonPrimitive(true)
                )
            )
        )
    }

    @RequestMapping("/v1/json-echo")
    fun jsonEcho(@RequestBody body: JsonElement): ResponseEntity<JsonElement> {
        return ResponseEntity.ok(body)
    }
}