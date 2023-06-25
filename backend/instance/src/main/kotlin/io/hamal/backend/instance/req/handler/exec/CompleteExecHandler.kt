package io.hamal.backend.instance.req.handler.exec

import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.instance.service.cmd.ExecCmdService
import io.hamal.backend.instance.service.cmd.StateCmdService
import io.hamal.backend.instance.service.query.ExecQueryService
import io.hamal.lib.domain.StartedExec
import io.hamal.lib.domain.req.SubmittedCompleteExecReq
import org.springframework.stereotype.Component

@Component
class CompleteExecHandler(
    private val execQueryService: ExecQueryService,
    private val execCmdService: ExecCmdService,
    private val stateCmdService: StateCmdService,
) : ReqHandler<SubmittedCompleteExecReq>(SubmittedCompleteExecReq::class) {

    override fun invoke(req: SubmittedCompleteExecReq) {
        val cmdId = req.cmdId()

        val exec = execQueryService.get(req.execId)
        require(exec is StartedExec) { "Exec not in status Started" }

        if (exec.correlation != null) {
            stateCmdService.set(
                cmdId, StateCmdService.StateToSet(
                    correlation = exec.correlation!!,
                    payload = req.state
                )
            )
        }

        execCmdService.complete(
            cmdId = cmdId,
            startedExec = exec
        )
    }
}