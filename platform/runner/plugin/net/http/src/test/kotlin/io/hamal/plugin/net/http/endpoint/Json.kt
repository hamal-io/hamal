package io.hamal.plugin.net.http.endpoint

import io.hamal.lib.common.value.ValueArray
import io.hamal.lib.common.value.ValueObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
open class TestJsonController {

    @RequestMapping("/v1/json-empty-object", produces = ["application/json"])
    fun jsonEmptyObject(): ResponseEntity<ValueObject> {
        return ResponseEntity.ok(ValueObject.empty)
    }

    @RequestMapping("/v1/json-empty-array", produces = ["application/json"])
    fun jsonEmptyArray(): ResponseEntity<ValueArray> {
        return ResponseEntity.ok(ValueArray.empty)
    }

    @RequestMapping("/v1/json-array", produces = ["application/json"])
    fun jsonArray(): ResponseEntity<ValueArray> {
        return ResponseEntity.ok(
            ValueArray.builder()
                .append(23)
                .append(true)
                .append("24.23")
                .append("HamalRocks")
                .build()
        )
    }

    @RequestMapping("/v1/json-error", produces = ["application/json"])
    fun jsonError(): ResponseEntity<ValueObject> {

        return ResponseEntity.badRequest().body(
            ValueObject.builder()
                .set("code", 400)
                .set("message", "bad-request")
                .set("boolean-value", true)
                .build()
        )
    }

    @RequestMapping("/v1/json-echo", produces = ["application/json"])
    fun jsonEcho(@RequestBody body: ValueObject): ResponseEntity<ValueObject> {
        return ResponseEntity.ok(body)
    }
}