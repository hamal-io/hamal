package io.hamal.plugin.net.http.endpoint

import io.hamal.lib.common.value.ValueArray
import io.hamal.lib.common.value.ValueDecimal
import io.hamal.lib.common.value.ValueObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
open class TestHonController {

    @RequestMapping("/v1/hon-empty-object", consumes = ["application/hon"], produces = ["application/hon"])
    fun honEmptyObject(): ResponseEntity<ValueObject> {
        return ResponseEntity.ok(ValueObject.empty)
    }

    @RequestMapping("/v1/hon-empty-array", consumes = ["application/hon"], produces = ["application/hon"])
    fun honEmptyArray(): ResponseEntity<ValueArray> {
        return ResponseEntity.ok(ValueArray.empty)
    }

    @RequestMapping("/v1/hon-array", consumes = ["application/hon"], produces = ["application/hon"])
    fun honArray(): ResponseEntity<ValueArray> {
        return ResponseEntity.ok(
            ValueArray.builder()
                .append(23)
                .append(true)
                .append("24.23")
                .append("HamalRocks")
                .append(ValueDecimal(13.37))
                .build()
        )
    }

    @RequestMapping("/v1/hon-error", consumes = ["application/hon"], produces = ["application/hon"])
    fun honError(): ResponseEntity<ValueObject> {

        return ResponseEntity.badRequest().body(
            ValueObject.builder()
                .set("code", 400)
                .set("message", "bad-request")
                .set("boolean-value", true)
                .build()
        )
    }

    @RequestMapping("/v1/hon-echo", consumes = ["application/hon"], produces = ["application/hon"])
    fun honEcho(@RequestBody body: ValueObject): ResponseEntity<ValueObject> {
        return ResponseEntity.ok(body)
    }
}