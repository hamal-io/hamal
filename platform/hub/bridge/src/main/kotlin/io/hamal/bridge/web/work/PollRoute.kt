package io.hamal.bridge.web.work

import io.hamal.core.event.HubEventEmitter
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.State
import io.hamal.lib.sdk.hub.HubUnitOfWorkList
import io.hamal.lib.sdk.hub.HubUnitOfWorkList.UnitOfWork
import io.hamal.repository.api.ExecCmdRepository
import io.hamal.repository.api.ExecCmdRepository.StartCmd
import io.hamal.repository.api.StateQueryRepository
import io.hamal.repository.api.event.ExecutionStartedEvent
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class PollRoute(
    private val execCmdRepository: ExecCmdRepository,
    private val stateQueryRepository: StateQueryRepository,
    private val eventEmitter: HubEventEmitter
) {
    @PostMapping("/v1/dequeue")
    fun dequeue(): ResponseEntity<HubUnitOfWorkList> {
        val cmdId = CmdId.random()
        val result = execCmdRepository.start(StartCmd(cmdId)).also {
            emitEvents(cmdId, it)
        }

        return ResponseEntity(
            HubUnitOfWorkList(
                work = result.map { exec ->
                    val state = exec.correlation?.let { stateQueryRepository.find(it)?.value } ?: State()
                    UnitOfWork(
                        id = exec.id,
                        groupId = exec.groupId,
                        correlation = exec.correlation,
                        inputs = exec.inputs,
                        state = state,
                        code = exec.code,
                        events = exec.events
                    )
                }), OK
        )
    }

    private fun emitEvents(cmdId: CmdId, execs: List<io.hamal.repository.api.StartedExec>) {
        execs.forEach { eventEmitter.emit(cmdId, ExecutionStartedEvent(it)) }
    }
}