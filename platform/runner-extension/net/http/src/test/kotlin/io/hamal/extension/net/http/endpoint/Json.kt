package io.hamal.extension.net.http.endpoint

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
open class TestJsonController {

    @RequestMapping("/v1/json-empty-object")
    fun jsonEmptyObject(): ResponseEntity<JsonObject> {
        TODO()
//        return ResponseEntity.ok(JsonObject(mapOf()))
    }

    @RequestMapping("/v1/json-empty-array")
    fun jsonEmptyArray(): ResponseEntity<JsonArray> {
        TODO()
//        return ResponseEntity.ok(JsonArray(listOf()))
    }

    @RequestMapping("/v1/json-array")
    fun jsonArray(): ResponseEntity<JsonArray> {
        TODO()
//        return ResponseEntity.ok(
//            JsonArray(
//                listOf(
//                    JsonPrimitive(23),
//                    JsonPrimitive(true),
//                    JsonPrimitive("24.23"),
//                    JsonPrimitive("HamalRocks")
//                )
//            )
//        )
    }

    @RequestMapping("/v1/json-error")
    fun jsonError(): ResponseEntity<JsonObject> {
        TODO()
//        return ResponseEntity.badRequest().body(
//            JsonObject(
//                mapOf(
//                    "code" to JsonPrimitive(400),
//                    "message" to JsonPrimitive("bad-request"),
//                    "boolean-value" to JsonPrimitive(true)
//                )
//            )
//        )
    }

    @RequestMapping("/v1/json-echo")
    fun jsonEcho(@RequestBody body: JsonElement): ResponseEntity<JsonElement> {
        return ResponseEntity.ok(body)
    }
}