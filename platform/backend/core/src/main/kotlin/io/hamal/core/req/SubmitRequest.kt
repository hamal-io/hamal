package io.hamal.core.req

import io.hamal.core.adapter.GroupListPort
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ReqId
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.ReqCmdRepository
import io.hamal.repository.api.submitted_req.ExecCompleteSubmitted
import io.hamal.repository.api.submitted_req.ExecFailSubmitted
import io.hamal.request.CompleteExecReq
import io.hamal.request.FailExecReq
import org.springframework.stereotype.Component


@Component
class SubmitRequest(
    private val funcQueryRepository: FuncQueryRepository,
    private val generateDomainId: GenerateDomainId,
    private val reqCmdRepository: ReqCmdRepository,
    private val groupList: GroupListPort
) {

    operator fun invoke(execId: ExecId, req: CompleteExecReq) = ExecCompleteSubmitted(
        id = generateDomainId(::ReqId),
        status = Submitted,
        execId = execId,
        result = req.result,
        state = req.state,
        events = req.events
    ).also(reqCmdRepository::queue)

    operator fun invoke(execId: ExecId, req: FailExecReq) = ExecFailSubmitted(
        id = generateDomainId(::ReqId),
        status = Submitted,
        execId = execId,
        result = req.result
    ).also(reqCmdRepository::queue)

}
