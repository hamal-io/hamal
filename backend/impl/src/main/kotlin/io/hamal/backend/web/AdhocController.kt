package io.hamal.backend.web

import io.hamal.backend.service.cmd.AdhocCmdService
import io.hamal.backend.service.cmd.AdhocCmdService.AdhocToExecute
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
    @Autowired val cmdService: AdhocCmdService
) {

    var counter: Int = 0

    @PostMapping("/v1/adhoc")
    fun adhoc(@RequestBody script: String): ResponseEntity<String> {
        cmdService.execute(
            AdhocToExecute(
                reqId = ReqId(counter++),
                shard = Shard(2),
                code = Code(script)
            )
        )
        return ResponseEntity.ok("ok")
    }
}