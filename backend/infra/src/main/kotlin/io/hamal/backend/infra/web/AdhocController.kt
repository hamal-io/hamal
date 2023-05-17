package io.hamal.backend.infra.web

import io.hamal.backend.usecase.request.AdhocRequest
import io.hamal.lib.domain.RequestId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.InvokeRequestOneUseCasePort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
open class AdhocController(
    @Autowired val request: InvokeRequestOneUseCasePort
) {

    var counter : Int = 0

    @PostMapping("/v1/adhoc")
    fun adhoc(@RequestBody script: String): ResponseEntity<String> {
        request(
            AdhocRequest.ExecuteJAdhocJob(
                requestId = RequestId(counter++),
                shard = Shard(2),
                script = script
            )
        )
        return ResponseEntity.ok("ok")
    }
}