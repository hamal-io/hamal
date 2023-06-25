package io.hamal.backend.instance.req.handler.exec

import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.instance.service.cmd.ExecCmdService
import io.hamal.backend.instance.service.cmd.StateCmdService
import io.hamal.backend.instance.service.query.ExecQueryService
import io.hamal.lib.domain.StartedExec
import io.hamal.lib.domain.req.CompleteExecReq
import io.hamal.lib.domain.req.SubmittedCompleteExecReq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CompleteExecHandler(
    @Autowired private val execQueryService: ExecQueryService,
    @Autowired private val execCmdService: ExecCmdService,
    @Autowired private val stateCmdService: StateCmdService,
) : ReqHandler<SubmittedCompleteExecReq>(SubmittedCompleteExecReq::class) {


    override fun invoke(req: SubmittedCompleteExecReq) {
        val cmdId = req.cmdId()

        execQueryService.find(req.execId)
            ?.let { exec ->
                if (exec is StartedExec) {

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
    }
}