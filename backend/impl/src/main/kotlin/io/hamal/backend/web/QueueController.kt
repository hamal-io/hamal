package io.hamal.backend.web

import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.backend.service.query.StateQueryService
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.sdk.domain.ApiAgentExecRequests
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController

class QueueController
@Autowired constructor(
    val execCmdService: ExecCmdService,
    val stateQueryService: StateQueryService
) {
    @PostMapping("/v1/dequeue")
    fun dequeueExec(): ApiAgentExecRequests {

        val result = execCmdService.dequeue(
            ExecCmdService.ToDequeue(
                reqId = ReqId(1111),
                shard = Shard(0),
            )
        )

        return ApiAgentExecRequests(
            requests = result.map {
                ApiAgentExecRequests.ExecRequest(
                    id = it.id,
                    funcName = FuncName("some-name"),
                    correlation = it.correlation,
                    statePayload = it.correlation?.let { stateQueryService.find(it) }?.payload,
                    code = it.code
                )
            })
    }
}