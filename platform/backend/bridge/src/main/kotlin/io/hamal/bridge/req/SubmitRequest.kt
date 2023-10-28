package io.hamal.bridge.req

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.api.ApiCompleteExecReq
import io.hamal.lib.sdk.api.ApiFailExecReq
import io.hamal.repository.api.ExecQueryRepository
import io.hamal.repository.api.ReqCmdRepository
import io.hamal.repository.api.submitted_req.SubmittedCompleteExecReq
import io.hamal.repository.api.submitted_req.SubmittedFailExecReq
import org.springframework.stereotype.Component

@Component
internal class SubmitBridgeRequest(
    private val generateDomainId: GenerateDomainId,
    private val reqCmdRepository: ReqCmdRepository,
    private val execQueryRepository: ExecQueryRepository
) {

    operator fun invoke(execId: ExecId, req: ApiCompleteExecReq): SubmittedCompleteExecReq {
        val exec = execQueryRepository.get(execId)
        return SubmittedCompleteExecReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = exec.id,
            result = req.result,
            state = req.state,
            events = req.events
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(execId: ExecId, req: ApiFailExecReq): SubmittedFailExecReq {
        val exec = execQueryRepository.get(execId)
        return SubmittedFailExecReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = exec.id,
            result = req.result
        ).also(reqCmdRepository::queue)
    }
}
