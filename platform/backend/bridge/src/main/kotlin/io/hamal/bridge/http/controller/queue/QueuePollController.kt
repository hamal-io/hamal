package io.hamal.bridge.http.controller.queue

import io.hamal.core.adapter.auth.AuthGetExecTokenPort
import io.hamal.core.event.InternalEventEmitter
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.GenerateCmdId
import io.hamal.lib.domain.State
import io.hamal.lib.sdk.bridge.BridgeUnitOfWorkList
import io.hamal.lib.sdk.bridge.BridgeUnitOfWorkList.UnitOfWork
import io.hamal.repository.api.CodeQueryRepository
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecCmdRepository
import io.hamal.repository.api.ExecCmdRepository.StartCmd
import io.hamal.repository.api.StateQueryRepository
import io.hamal.repository.api.event.ExecStartedEvent
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class QueuePollController(
    private val codeQueryRepository: CodeQueryRepository,
    private val execCmdRepository: ExecCmdRepository,
    private val stateQueryRepository: StateQueryRepository,
    private val eventEmitter: InternalEventEmitter,
    private val generateCmdId: GenerateCmdId,
    private val getExecToken: AuthGetExecTokenPort
) {
    @PostMapping("/v1/dequeue")
    fun dequeue(): ResponseEntity<BridgeUnitOfWorkList> {
        val cmdId = generateCmdId()

        val result = execCmdRepository.start(StartCmd(cmdId)).also {
            emitEvents(cmdId, it)
        }
        return ResponseEntity(
            BridgeUnitOfWorkList(work = result.map { exec ->
                val state = exec.correlation?.let { stateQueryRepository.find(it)?.value } ?: State()

                val code = exec.code.value ?: run {
                    codeQueryRepository.find(exec.code.id!!, exec.code.version!!)?.value
                }!!

                UnitOfWork(
                    id = exec.id,
                    execToken = getExecToken(exec.id).token,
                    namespaceId = exec.namespaceId,
                    workspaceId = exec.workspaceId,
                    correlation = exec.correlation,
                    inputs = exec.inputs,
                    state = state,
                    code = code
                )
            }), OK
        )
    }

    private fun emitEvents(cmdId: CmdId, execs: List<Exec.Started>) {
        execs.forEach { eventEmitter.emit(cmdId, ExecStartedEvent(it)) }
    }
}