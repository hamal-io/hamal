package io.hamal.backend.web

import io.hamal.backend.event.OneshotInvocationEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.domain.Func
import io.hamal.backend.repository.api.domain.Tenant
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.backend.service.cmd.FuncCmdService
import io.hamal.backend.service.query.FuncQueryService
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.domain.ApiCreateFuncRequest
import io.hamal.lib.sdk.domain.ApiExecFuncRequest
import io.hamal.lib.sdk.domain.ApiExecFuncResponse
import io.hamal.lib.sdk.domain.ApiListFuncResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
open class FuncController(
    @Autowired val queryService: FuncQueryService,
    @Autowired val funcCmdService: FuncCmdService,
    @Autowired val execCmdService: ExecCmdService,
    @Autowired val eventEmitter: EventEmitter
) {
    @PostMapping("/v1/funcs")
    fun createFunc(
        @RequestAttribute shard: Shard,
        @RequestAttribute reqId: ReqId,
        @RequestAttribute tenant: Tenant,
        @RequestBody req: ApiCreateFuncRequest
    ): Func {
        // FIXME to ApiCreateFuncResponse
        return funcCmdService.create(
            FuncCmdService.FuncToCreate(
                reqId = reqId,
                shard = shard,
                name = req.name,
                code = req.code
            )
        )
    }

    @GetMapping("/v1/funcs")
    fun listFunc(
        @RequestParam(required = false, name = "after_id", defaultValue = "0") stringFuncId: String,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Int
    ): ResponseEntity<ApiListFuncResponse> {
        val result = queryService.list(
            afterId = FuncId(SnowflakeId(stringFuncId.toLong())),
            limit = limit
        )

        return ResponseEntity.ok(
            ApiListFuncResponse(
                result.map {
                    ApiListFuncResponse.Func(
                        id = it.id,
                        name = it.name
                    )
                }
            )
        )
    }

    @PostMapping("/v1/funcs/{funcId}/exec")
    fun execFunc(
        @RequestAttribute shard: Shard,
        @RequestAttribute reqId: ReqId,
        @PathVariable("funcId") stringFuncId: String,
        @RequestBody body: ApiExecFuncRequest
    ): ResponseEntity<ApiExecFuncResponse> { // prob 202

        //FIXME should be  a service
        val funcId = FuncId(SnowflakeId(stringFuncId.replace("'", "").toLong()))
        val func = queryService.get(funcId)

        eventEmitter.emit(
            OneshotInvocationEvent(
                reqId = reqId,
                shard = shard,
                func = func
            )
        )

        return ResponseEntity.ok(
            ApiExecFuncResponse(
                reqId = reqId
            )
        )
    }
}