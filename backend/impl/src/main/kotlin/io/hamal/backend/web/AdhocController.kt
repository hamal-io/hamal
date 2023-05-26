package io.hamal.backend.web

import io.hamal.backend.event.AdhocInvocationEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.Code
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
open class AdhocController(
    @Autowired val eventEmitter: EventEmitter
) {

    var counter: Int = 0

    @PostMapping("/v1/adhoc")
    fun adhoc(@RequestBody script: String): ResponseEntity<String> {
        eventEmitter.emit(
            AdhocInvocationEvent(
                reqId = ReqId(counter),
                shard = Shard(0),
                code = Code(script)
                // FIXME invoked at
                // FIXME invoked by

            )
        )
        return ResponseEntity.ok("ok")
    }
}