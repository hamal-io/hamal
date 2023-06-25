package io.hamal.backend.instance.web

import io.hamal.backend.instance.service.cmd.ExecCmdService
import io.hamal.backend.instance.service.query.StateQueryService
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.sdk.domain.ApiAgentExecRequests
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger
import java.security.SecureRandom

@RestController
class QueueController
@Autowired constructor(
    val execCmdService: ExecCmdService,
    val stateQueryService: StateQueryService
) {
    @PostMapping("/v1/dequeue")
    fun dequeueExec(): ApiAgentExecRequests {

        //FIXME is this the only request which can not be handled in typical request manner?
        val result = execCmdService.start(CmdId(BigInteger(128, SecureRandom())))

        return ApiAgentExecRequests(
            requests = result.map {
                ApiAgentExecRequests.ExecRequest(
                    id = it.id,
                    funcName = FuncName("some-name"),
                    correlation = it.correlation,
                    inputs = it.inputs,
                    secrets = it.secrets,
                    statePayload = it.correlation?.let { stateQueryService.find(it) }?.state,
                    code = it.code
                )
            })
    }
}