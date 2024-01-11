package io.hamal.extension.net.http.endpoint

import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotNode
import io.hamal.lib.common.hot.HotObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
open class TestJsonController {

    @RequestMapping("/v1/json-empty-object")
    fun jsonEmptyObject(): ResponseEntity<HotObject> {
        return ResponseEntity.ok(HotObject.empty)
    }

    @RequestMapping("/v1/json-empty-array")
    fun jsonEmptyArray(): ResponseEntity<HotArray> {
        return ResponseEntity.ok(HotArray.empty)
    }

    @RequestMapping("/v1/json-array")
    fun jsonArray(): ResponseEntity<HotArray> {
        return ResponseEntity.ok(
            HotArray.builder()
                .add(23)
                .add(true)
                .add("24.23")
                .add("HamalRocks")
                .build()
        )
    }

    @RequestMapping("/v1/json-error")
    fun jsonError(): ResponseEntity<HotObject> {
        return ResponseEntity.badRequest().body(
            HotObject.builder()
                .set("code", 400)
                .set("message", "bad-request")
                .set("boolean-value", true)
                .build()
        )
    }

    @RequestMapping("/v1/json-echo")
    fun jsonEcho(@RequestBody body: HotNode): ResponseEntity<HotNode> {
        return ResponseEntity.ok(body)
    }
}