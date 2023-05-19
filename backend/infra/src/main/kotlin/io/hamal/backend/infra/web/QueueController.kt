package io.hamal.backend.infra.web

import io.hamal.backend.usecase.request.ExecRequest
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.InvokeRequestManyUseCasePort
import io.hamal.lib.domain.vo.FuncRef
import io.hamal.lib.sdk.domain.ApiExecution
import io.hamal.lib.sdk.domain.ApiExecutions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController

class QueueController
@Autowired constructor(
    val requestMany: InvokeRequestManyUseCasePort
) {
    @PostMapping("/v1/dequeue")
    fun dequeueExec(): ApiExecutions {

        val result = requestMany.invoke(
            ExecRequest.DequeueExec(
                reqId = ReqId(1111),
                shard = Shard(0)
            )
        )

        return ApiExecutions(
            executions = result.map {
                ApiExecution(
                    id = it.id,
                    reference = FuncRef("ref"),
                    code = it.trigger.func.code
                )
            })
    }
}