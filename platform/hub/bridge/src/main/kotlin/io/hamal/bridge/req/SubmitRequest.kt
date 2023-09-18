package io.hamal.bridge.req

import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateToken
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.hub.HubCompleteExecReq
import io.hamal.lib.sdk.hub.HubFailExecReq
import io.hamal.repository.api.ExecQueryRepository
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.ReqCmdRepository
import io.hamal.repository.api.submitted_req.SubmittedCompleteExecReq
import io.hamal.repository.api.submitted_req.SubmittedFailExecReq
import org.springframework.stereotype.Component

@Component
internal class SubmitBridgeRequest(
    private val generateDomainId: GenerateDomainId,
    private val reqCmdRepository: ReqCmdRepository,
    private val funcQueryRepository: FuncQueryRepository,
    private val encodePassword: EncodePassword,
    private val generateToken: GenerateToken,
    private val execQueryRepository: ExecQueryRepository
) {

    operator fun invoke(execId: ExecId, req: HubCompleteExecReq): SubmittedCompleteExecReq {
        val exec = execQueryRepository.get(execId)
        return SubmittedCompleteExecReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = execId,
            groupId = exec.groupId,
            state = req.state,
            events = req.events
        ).also(reqCmdRepository::queue)
    }

    operator fun invoke(execId: ExecId, req: HubFailExecReq): SubmittedFailExecReq {
        val exec = execQueryRepository.get(execId)
        return SubmittedFailExecReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = execId,
            groupId = exec.groupId,
            cause = req.cause
        ).also(reqCmdRepository::queue)
    }

}
