package io.hamal.backend.web

import io.hamal.backend.event.AgentCompletedEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.backend.service.query.ExecQueryService
import io.hamal.lib.common.Shard
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.StatePayload
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.domain.ApiDetailExecutionModel
import io.hamal.lib.sdk.domain.ApiSimpleExecutionModel
import io.hamal.lib.sdk.domain.ApiSimpleExecutionModels
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
open class ExecController(
    @Autowired val queryService: ExecQueryService,
    @Autowired val cmdService: ExecCmdService,
    @Autowired val eventEmitter: EventEmitter
) {

    @GetMapping("/v1/execs/{execId}")
    fun get(
        @PathVariable("execId") stringExecId: String // FIXME be able to use value objects directly here
    ): ResponseEntity<ApiDetailExecutionModel> {
        val result = queryService.get(ExecId(SnowflakeId(stringExecId.toLong())))
        return ResponseEntity.ok(
            ApiDetailExecutionModel(
                id = result.id,
                state = result.state,
                code = result.code,
            )
        )
    }


    @GetMapping("/v1/execs")
    fun query(
        @RequestParam(required = false, name = "after_id", defaultValue = "0") stringExecId: String,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Int
    ): ResponseEntity<ApiSimpleExecutionModels> {
        val result = queryService.list(
            afterId = ExecId(SnowflakeId(stringExecId.toLong())),
            limit = limit
        )

        return ResponseEntity.ok(
            ApiSimpleExecutionModels(
                result.map {
                    ApiSimpleExecutionModel(
                        id = it.id,
                        state = it.state
                    )
                }
            )
        )
    }

    @PostMapping("/v1/execs/{execId}/complete")
    fun completeExec(
        @PathVariable("execId") stringExecId: String, // FIXME be able to use value objects directly here
        @RequestHeader("Content-Type") contentType: String,
        @RequestBody bytes: ByteArray
    ) {

        println("completing exec $stringExecId")
        val execId = ExecId(SnowflakeId(stringExecId.toLong()))

        eventEmitter.emit(
            AgentCompletedEvent(
                reqId = ReqId(123),
                shard = Shard(1),
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