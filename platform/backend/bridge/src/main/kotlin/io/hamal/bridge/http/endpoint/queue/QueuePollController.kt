package io.hamal.bridge.http.endpoint.queue

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.State
import io.hamal.lib.sdk.api.ApiUnitOfWorkList
import io.hamal.lib.sdk.api.ApiUnitOfWorkList.UnitOfWork
import io.hamal.repository.api.CodeQueryRepository
import io.hamal.repository.api.ExecCmdRepository
import io.hamal.repository.api.ExecCmdRepository.StartCmd
import io.hamal.repository.api.StateQueryRepository
import io.hamal.repository.api.event.ExecutionStartedEvent
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class QueuePollController(
    private val codeQueryRepository: CodeQueryRepository,
    private val execCmdRepository: ExecCmdRepository,
    private val stateQueryRepository: StateQueryRepository,
    private val eventEmitter: PlatformEventEmitter
) {
    @PostMapping("/v1/dequeue")
    fun dequeue(): ResponseEntity<ApiUnitOfWorkList> {
        val cmdId = CmdId.random()
        val result = execCmdRepository.start(StartCmd(cmdId)).also {
            emitEvents(cmdId, it)
        }
        return ResponseEntity(
            ApiUnitOfWorkList(
                work = result.map { exec ->
                    val state = exec.correlation?.let { stateQueryRepository.find(it)?.value } ?: State()

                    val code = exec.code.value ?: run {
                        codeQueryRepository.find(exec.code.id!!, exec.code.version!!)?.value
                    }!!

                    UnitOfWork(
                        id = exec.id,
                        namespaceId = exec.namespaceId,
                        groupId = exec.groupId,
                        correlation = exec.correlation,
                        inputs = exec.inputs,
                        state = state,
                        code = code,
                        events = exec.events
                    )
                }), OK
        )
    }

    private fun emitEvents(cmdId: CmdId, execs: List<io.hamal.repository.api.StartedExec>) {
        execs.forEach { eventEmitter.emit(cmdId, ExecutionStartedEvent(it)) }
    }
}