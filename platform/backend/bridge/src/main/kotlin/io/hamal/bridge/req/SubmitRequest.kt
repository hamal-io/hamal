package io.hamal.bridge.req

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.bridge.BridgeExecCompleteReq
import io.hamal.lib.sdk.bridge.BridgeExecFailReq
import io.hamal.repository.api.ExecQueryRepository
import io.hamal.repository.api.ReqCmdRepository
import io.hamal.repository.api.submitted_req.ExecCompleteSubmitted
import io.hamal.repository.api.submitted_req.ExecFailSubmitted
import org.springframework.stereotype.Component

@Component
internal class SubmitBridgeRequest(
    private val generateDomainId: GenerateDomainId,
    private val reqCmdRepository: ReqCmdRepository,
    private val execQueryRepository: ExecQueryRepository
) {

    operator fun invoke(execId: ExecId, req: BridgeExecCompleteReq): ExecCompleteSubmitted {
        val exec = execQueryRepository.get(execId)
        return ExecCompleteSubmitted(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            execId = exec.id,
            result = req.result,
            state = req.state,
            events = req.events
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(execId: ExecId, req: BridgeExecFailReq): ExecFailSubmitted {
        val exec = execQueryRepository.get(execId)
        return ExecFailSubmitted(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            execId = exec.id,
            result = req.result
        ).also(reqCmdRepository::queue)
    }
}
