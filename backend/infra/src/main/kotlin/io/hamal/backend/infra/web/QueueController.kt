package io.hamal.backend.infra.web

import io.hamal.backend.infra.usecase.request.ExecRequest
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.InvokeRequestManyUseCasePort
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.sdk.domain.ApiAgentRequest
import io.hamal.lib.sdk.domain.ApiAgentRequests
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController

class QueueController
@Autowired constructor(
    val requestMany: InvokeRequestManyUseCasePort
) {
    @PostMapping("/v1/dequeue")
    fun dequeueExec(): ApiAgentRequests {

        val result = requestMany.invoke(
            ExecRequest.DequeueExec(
                reqId = ReqId(1111),
                shard = Shard(0)
            )
        )

        return ApiAgentRequests(
            requests = result.map {
                ApiAgentRequest(
                    id = it.id,
                    reference = FuncName("ref"),
                    code = it.code
                )
            })
    }
}