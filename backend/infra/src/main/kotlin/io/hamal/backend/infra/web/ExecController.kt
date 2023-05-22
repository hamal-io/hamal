package io.hamal.backend.infra.web

import io.hamal.backend.usecase.query.ExecQuery
import io.hamal.backend.usecase.request.ExecRequest
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.InvokeQueryManyUseCasePort
import io.hamal.lib.domain.ddd.InvokeQueryOneUseCasePort
import io.hamal.lib.domain.ddd.InvokeRequestOneUseCasePort
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.TriggerRef
import io.hamal.lib.sdk.domain.ApiDetailExecutionModel
import io.hamal.lib.sdk.domain.ApiDetailExecutionModel.CauseModel
import io.hamal.lib.sdk.domain.ApiDetailExecutionModel.FunctionModel
import io.hamal.lib.sdk.domain.ApiSimpleExecutionModel
import io.hamal.lib.sdk.domain.ApiSimpleExecutionModels
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
open class ExecController(
    @Autowired val queryOne: InvokeQueryOneUseCasePort,
    @Autowired val queryMany: InvokeQueryManyUseCasePort,
    @Autowired val request: InvokeRequestOneUseCasePort
) {

    @GetMapping("/v1/executions/{execId}")
    fun get(
        @PathVariable("execId") stringExecId: String // FIXME be able to use value objects directly here
    ): ResponseEntity<ApiDetailExecutionModel> {
        val result = queryOne(
            ExecQuery.GetExec(execId = ExecId(SnowflakeId(stringExecId.toLong())))
        )
        return ResponseEntity.ok(
            ApiDetailExecutionModel(
                id = result.id,
                ref = result.func.reference,
                state = result.state,
                func = FunctionModel(
                    id = result.func.id,
                    code = result.func.code
                ),
                cause = CauseModel(
                    id = result.cause.id,
                    ref = TriggerRef("TBD")
                )
            )
        )
    }


    @GetMapping("/v1/executions")
    fun query(
        @RequestParam(required = false, name = "after_id", defaultValue = "0") stringExecId: String,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Int
    ): ResponseEntity<ApiSimpleExecutionModels> {
        val result = queryMany(
            ExecQuery.ListExec(
                afterId = ExecId(SnowflakeId(stringExecId.toLong())),
                limit = limit
            )
        )
        return ResponseEntity.ok(
            ApiSimpleExecutionModels(
                result.map {
                    ApiSimpleExecutionModel(
                        id = it.id,
                        ref = it.func.reference,
                        state = it.state
                    )
                }
            )
        )
    }

    @PostMapping("/v1/executions/{execId}/complete")
    fun completeExec(
        @PathVariable("execId") stringExecId: String // FIXME be able to use value objects directly here
    ) {
        println("completing exec $stringExecId")
        val execId = ExecId(SnowflakeId(stringExecId.toLong()))

        val startedExec = queryOne(ExecQuery.GetStartedExec(execId))
        request(
            ExecRequest.CompleteStartedExec(
                reqId = ReqId(1234),
                shard = Shard(execId.partition().value.toInt()), //FIXME
                startedExec = startedExec
            )
        )
    }

    @PostMapping("/v1/executions/{execId}/fail")
    fun failExec(
        @PathVariable("execId") stringExecId: String
    ) {
        println("failing exec $stringExecId")
    }
}