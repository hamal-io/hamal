package io.hamal.plugin.net.http.endpoint

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.hamal.lib.common.serialization.serde.SerdeArray
import io.hamal.lib.common.serialization.serde.SerdeObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
open class TestJsonController {

    @RequestMapping("/v1/json-empty-object")
    fun jsonEmptyObject(): ResponseEntity<JsonObject> {
        return ResponseEntity.ok(JsonObject())
    }

    @RequestMapping("/v1/json-empty-array")
    fun jsonEmptyArray(): ResponseEntity<JsonArray> {
        return ResponseEntity.ok(JsonArray())
    }

    @RequestMapping("/v1/json-array")
    fun jsonArray(): ResponseEntity<SerdeArray> {
        return ResponseEntity.ok(
            SerdeArray.builder()
                .append(23)
                .append(true)
                .append("24.23")
                .append("HamalRocks")
                .build()
        )
    }

    @RequestMapping("/v1/json-error")
    fun jsonError(): ResponseEntity<SerdeObject> {

        return ResponseEntity.badRequest().body(
            SerdeObject.builder()
                .set("code", 400)
                .set("message", "bad-request")
                .set("boolean-value", true)
                .build()
        )
    }

    @RequestMapping("/v1/json-echo")
    fun jsonEcho(@RequestBody body: JsonElement): ResponseEntity<JsonElement> {
        return ResponseEntity.ok(body)
    }
}