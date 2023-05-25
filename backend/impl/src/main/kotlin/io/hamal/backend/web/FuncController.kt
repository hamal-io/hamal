package io.hamal.backend.web

import io.hamal.backend.service.cmd.FuncCmdService
import io.hamal.backend.service.query.FuncQueryService
import io.hamal.backend.repository.api.domain.func.Func
import io.hamal.backend.repository.api.domain.tenant.Tenant
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.domain.ApiCreateFuncRequest
import io.hamal.lib.sdk.domain.ApiListFuncResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
open class FuncController(
    @Autowired val queryService: FuncQueryService,
    @Autowired val cmdService: FuncCmdService
) {
    @PostMapping("/v1/funcs")
    fun createFunc(
        @RequestAttribute shard: Shard,
        @RequestAttribute reqId: ReqId,
        @RequestAttribute tenant: Tenant,
        @RequestBody req: ApiCreateFuncRequest
    ): Func {
        // FIXME to ApiCreateFuncResponse
        return cmdService.create(
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
}