package io.hamal.backend.event_handler.agent

import io.hamal.backend.event.AgentCompletedEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.repository.api.domain.InFlightExec
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.backend.service.cmd.StateCmdService
import io.hamal.backend.service.query.ExecQueryService
import org.springframework.beans.factory.annotation.Autowired

class AgentCompletedHandler(
    @Autowired val execQueryService: ExecQueryService,
    @Autowired val execCmdService: ExecCmdService,
    @Autowired val stateCmdService: StateCmdService
) : EventHandler<AgentCompletedEvent> {
    override fun handle(evt: AgentCompletedEvent) {
        println("agent completed exec ${evt.execId}")

        execQueryService.find(evt.execId)
            ?.let { exec ->
                if (exec is InFlightExec) {

                    if (exec.correlation != null) {
                        stateCmdService.set(
                            evt.reqId, StateCmdService.StateToSet(
                                shard = evt.shard,
                                correlation = exec.correlation!!,
                                payload = evt.statePayload
                            )
                        )
                    }

                    execCmdService.complete(
                        ExecCmdService.ToComplete(
                            reqId = evt.reqId,
                            shard = evt.shard, //FIXME
                            inFlightExec = exec
                        )
                    )
                }
            }
    }
}