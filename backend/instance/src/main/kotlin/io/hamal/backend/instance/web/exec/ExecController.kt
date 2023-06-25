package io.hamal.backend.instance.web.exec

import io.hamal.backend.instance.component.SystemEventEmitter
import io.hamal.backend.instance.req.CompleteExec
import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.backend.instance.service.cmd.ExecCmdService
import io.hamal.backend.instance.service.query.ExecQueryService
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.StatePayload
import io.hamal.lib.domain.vo.ExecId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
open class ExecController(
    @Autowired val queryService: ExecQueryService,
    @Autowired val cmdService: ExecCmdService,
    @Autowired val eventEmitter: SystemEventEmitter<*>,
    @Autowired val request: SubmitRequest
) {
    @PostMapping("/v1/execs/{execId}/complete")
    fun completeExec(
        @PathVariable("execId") stringExecId: String, // FIXME be able to use value objects directly here
        @RequestHeader("Content-Type") contentType: String,
        @RequestBody bytes: ByteArray
    ) {
        println("completing exec $stringExecId")
        val execId = ExecId(SnowflakeId(stringExecId.toLong()))

        request(
            CompleteExec(
                execId = execId,
                statePayload = StatePayload(
                    contentType = contentType,
                    bytes = bytes
                )
            )
        )
    }

    @PostMapping("/v1/execs/{execId}/fail")
    fun failExec(
        @PathVariable("execId") stringExecId: String
    ) {
        println("failing exec $stringExecId")
    }
}