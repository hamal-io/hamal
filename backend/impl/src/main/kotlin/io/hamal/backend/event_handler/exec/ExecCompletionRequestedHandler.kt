package io.hamal.backend.event_handler.exec

import io.hamal.backend.event.ExecCompletionRequestedEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.repository.api.domain.InFlightExec
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.backend.service.cmd.StateCmdService
import io.hamal.backend.service.query.ExecQueryService
import io.hamal.lib.domain.ReqId
import org.springframework.beans.factory.annotation.Autowired

class ExecCompletionRequestedHandler(
    @Autowired val execQueryService: ExecQueryService,
    @Autowired val execCmdService: ExecCmdService,
    @Autowired val stateCmdService: StateCmdService,
) : EventHandler<ExecCompletionRequestedEvent> {
    override fun handle(reqId: ReqId, evt: ExecCompletionRequestedEvent) {
        println("agent completed exec ${evt.execId}")

        execQueryService.find(evt.execId)
            ?.let { exec ->
                if (exec is InFlightExec) {

                    if (exec.correlation != null) {
                        stateCmdService.set(
                            reqId, StateCmdService.StateToSet(
                                shard = evt.shard,
                                correlation = exec.correlation!!,
                                payload = evt.statePayload
                            )
                        )
                    }

                    execCmdService.complete(
                        reqId = reqId,
                        inFlightExec = exec
                    )
                }
            }
    }
}