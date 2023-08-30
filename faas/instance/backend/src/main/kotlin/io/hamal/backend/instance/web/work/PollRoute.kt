package io.hamal.backend.instance.web.work

import io.hamal.backend.instance.event.InstanceEventEmitter
import io.hamal.backend.instance.event.events.ExecutionStartedEvent
import io.hamal.repository.api.ExecCmdRepository.StartCmd
import io.hamal.repository.api.StateQueryRepository
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.State
import io.hamal.lib.sdk.domain.ApiUnitOfWorkList
import io.hamal.lib.sdk.domain.ApiUnitOfWorkList.ApiUnitOfWork
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger
import java.security.SecureRandom

@RestController
class PollRoute(
    private val execCmdRepository: io.hamal.repository.api.ExecCmdRepository,
    private val stateQueryRepository: StateQueryRepository,
    private val eventEmitter: InstanceEventEmitter
) {
    @PostMapping("/v1/dequeue")
    fun dequeue(): ResponseEntity<ApiUnitOfWorkList> {
        val cmdId = CmdId(BigInteger(128, rnd))
        val result = execCmdRepository.start(StartCmd(cmdId)).also {
            emitEvents(cmdId, it)
        }

        return ResponseEntity(
            ApiUnitOfWorkList(
                work = result.map { exec ->
                    val state = exec.correlation?.let { stateQueryRepository.find(it)?.value } ?: State()
                    ApiUnitOfWork(
                        id = exec.id,
                        correlation = exec.correlation,
                        inputs = exec.inputs,
                        state = state,
                        code = exec.code,
                        invocation = exec.invocation
                    )
                }), OK
        )
    }

    private fun emitEvents(cmdId: CmdId, execs: List<io.hamal.repository.api.StartedExec>) {
        execs.forEach { eventEmitter.emit(cmdId, ExecutionStartedEvent(it)) }
    }

    private val rnd = SecureRandom.getInstance("SHA1PRNG", "SUN")
}