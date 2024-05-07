package io.hamal.extension.net.http.endpoint

import io.hamal.lib.common.serialization.json.JsonArray
import io.hamal.lib.common.serialization.json.JsonNode
import io.hamal.lib.common.serialization.json.JsonObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
open class TestJsonController {

    @RequestMapping("/v1/json-empty-object")
    fun jsonEmptyObject(): ResponseEntity<JsonObject> {
        return ResponseEntity.ok(JsonObject.empty)
    }

    @RequestMapping("/v1/json-empty-array")
    fun jsonEmptyArray(): ResponseEntity<JsonArray> {
        return ResponseEntity.ok(JsonArray.empty)
    }

    @RequestMapping("/v1/json-array")
    fun jsonArray(): ResponseEntity<JsonArray> {
        return ResponseEntity.ok(
            JsonArray.builder()
                .append(23)
                .append(true)
                .append("24.23")
                .append("HamalRocks")
                .build()
        )
    }

    @RequestMapping("/v1/json-error")
    fun jsonError(): ResponseEntity<JsonObject> {
        return ResponseEntity.badRequest().body(
            JsonObject.builder()
                .set("code", 400)
                .set("message", "bad-request")
                .set("boolean-value", true)
                .build()
        )
    }

    @RequestMapping("/v1/json-echo")
    fun jsonEcho(@RequestBody body: JsonNode<*>): ResponseEntity<JsonNode<*>> {
        return ResponseEntity.ok(body)
    }
}