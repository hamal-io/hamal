package io.hamal.backend.instance.web.queue

import io.hamal.backend.instance.event.ExecutionStartedEvent
import io.hamal.backend.instance.event.SystemEventEmitter
import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.ExecCmdRepository.*
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.StartedExec
import io.hamal.lib.domain.State
import io.hamal.lib.kua.value.TableValue
import io.hamal.lib.sdk.domain.DequeueExecsResponse
import io.hamal.lib.sdk.domain.DequeueExecsResponse.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger
import java.security.SecureRandom

@RestController
class DequeueRoute(
    private val execCmdRepository: ExecCmdRepository,
    private val eventEmitter: SystemEventEmitter<*>
) {
    @PostMapping("/v1/dequeue")
    fun dequeue(): ResponseEntity<DequeueExecsResponse> {
        val cmdId = CmdId(BigInteger(128, rnd))
        val result = execCmdRepository.start(StartCmd(cmdId)).also {
            emitEvents(cmdId, it)
        }
        return ResponseEntity(
            DequeueExecsResponse(
                execs = result.map {
                    Exec(
                        id = it.id,
                        correlation = it.correlation,
                        inputs = it.inputs,
                        state = State(TableValue()),//FIXME
                        code = it.code
                    )
                }), HttpStatus.OK
        )
    }

    private fun emitEvents(cmdId: CmdId, execs: List<StartedExec>) {
        execs.forEach { eventEmitter.emit(cmdId, ExecutionStartedEvent(it)) }
    }

    private val rnd = SecureRandom.getInstance("SHA1PRNG", "SUN")
}