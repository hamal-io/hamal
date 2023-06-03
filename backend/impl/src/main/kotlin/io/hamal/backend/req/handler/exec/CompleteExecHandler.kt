package io.hamal.backend.req.handler.exec

import io.hamal.backend.repository.api.domain.CompleteExecReq
import io.hamal.backend.repository.api.domain.InFlightExec
import io.hamal.backend.req.ReqHandler
import io.hamal.backend.req.handler.commandId
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.backend.service.cmd.StateCmdService
import io.hamal.backend.service.query.ExecQueryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CompleteExecHandler(
    @Autowired private val execQueryService: ExecQueryService,
    @Autowired private val execCmdService: ExecCmdService,
    @Autowired private val stateCmdService: StateCmdService,
) : ReqHandler<CompleteExecReq>(CompleteExecReq::class) {


    override fun invoke(req: CompleteExecReq) {
        val commandId = req.commandId()

        execQueryService.find(req.execId)
            ?.let { exec ->
                if (exec is InFlightExec) {

                    if (exec.correlation != null) {
                        stateCmdService.set(
                            commandId, StateCmdService.StateToSet(
                                shard = req.shard,
                                correlation = exec.correlation!!,
                                payload = req.statePayload
                            )
                        )
                    }

                    execCmdService.complete(
                        commandId = commandId,
                        inFlightExec = exec
                    )
                }
            }
    }
}