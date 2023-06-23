package io.hamal.backend.req.handler.exec

import io.hamal.backend.repository.api.domain.StartedExec
import io.hamal.backend.req.ReqHandler
import io.hamal.backend.req.handler.cmdId
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.backend.service.cmd.StateCmdService
import io.hamal.backend.service.query.ExecQueryService
import io.hamal.lib.domain.req.CompleteExecReq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CompleteExecHandler(
    @Autowired private val execQueryService: ExecQueryService,
    @Autowired private val execCmdService: ExecCmdService,
    @Autowired private val stateCmdService: StateCmdService,
) : ReqHandler<CompleteExecReq>(CompleteExecReq::class) {


    override fun invoke(req: CompleteExecReq) {
        val cmdId = req.cmdId()

        execQueryService.find(req.execId)
            ?.let { exec ->
                if (exec is StartedExec) {

                    if (exec.correlation != null) {
                        stateCmdService.set(
                            cmdId, StateCmdService.StateToSet(
                                correlation = exec.correlation!!,
                                payload = req.statePayload
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