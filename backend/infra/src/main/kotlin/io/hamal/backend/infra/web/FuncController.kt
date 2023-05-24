package io.hamal.backend.infra.web

import io.hamal.backend.core.func.Func
import io.hamal.backend.core.tenant.Tenant
import io.hamal.backend.usecase.query.FuncQuery
import io.hamal.backend.usecase.request.FuncRequest
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.InvokeQueryManyUseCasePort
import io.hamal.lib.domain.ddd.InvokeRequestOneUseCasePort
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.domain.ApiCreateFuncRequest
import io.hamal.lib.sdk.domain.ApiListFuncResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
open class FuncController(
    @Autowired val queryMany: InvokeQueryManyUseCasePort,
    @Autowired val request: InvokeRequestOneUseCasePort,
) {
    @PostMapping("/v1/funcs")
    fun createFunc(
        @RequestAttribute shard: Shard,
        @RequestAttribute reqId: ReqId,
        @RequestAttribute tenant: Tenant,
        @RequestBody req: ApiCreateFuncRequest
    ): Func {
        return request(
            FuncRequest.FuncCreation(
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
        val result = queryMany(
            FuncQuery.ListFunc(
                afterId = FuncId(SnowflakeId(stringFuncId.toLong())),
                limit = limit
            )
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