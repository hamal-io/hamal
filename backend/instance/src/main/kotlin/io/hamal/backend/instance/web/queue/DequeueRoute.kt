package io.hamal.backend.instance.web.queue

import io.hamal.backend.instance.service.cmd.ExecCmdService
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.Content
import io.hamal.lib.domain.vo.ContentType
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
    private val execCmdService: ExecCmdService,
) {
    @PostMapping("/v1/dequeue")
    fun dequeue(): ResponseEntity<DequeueExecsResponse> {
        val result = execCmdService.start(CmdId(BigInteger(128, rnd)))
        return ResponseEntity(
            DequeueExecsResponse(
                execs = result.map {
                    Exec(
                        id = it.id,
                        correlation = it.correlation,
                        inputs = it.inputs,
                        secrets = it.secrets,
                        state = State(ContentType(""), Content("")),//FIXME
                        code = it.code
                    )
                }), HttpStatus.OK
        )
    }

    private val rnd = SecureRandom.getInstance("SHA1PRNG", "SUN")
}