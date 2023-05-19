package io.hamal.backend.infra.web

import io.hamal.backend.usecase.query.ExecQuery
import io.hamal.backend.usecase.request.ExecRequest
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.InvokeQueryOneUseCasePort
import io.hamal.lib.domain.ddd.InvokeRequestOneUseCasePort
import io.hamal.lib.domain.vo.ExecId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
open class ExecController(
    @Autowired val queryOne: InvokeQueryOneUseCasePort,
    @Autowired val request: InvokeRequestOneUseCasePort
) {

    @PostMapping("/v1/execs/{execId}/complete")
    fun completeExec(
        @PathVariable("execId") stringExecId: String // FIXME be able to use value objects directly here
    ) {
        println("completing exec $stringExecId")
        val execId = ExecId(SnowflakeId(stringExecId.toLong()))

        val startedExec = queryOne(ExecQuery.GetStartedExec(execId))
        request(
            ExecRequest.CompleteStartedExec(
                reqId = ReqId(1234),
                shard = Shard(execId.partition().value.toInt()), //FIXME
                startedExec = startedExec
            )
        )
    }

    @PostMapping("/v1/execs/{execId}/fail")
    fun failExec(
        @PathVariable("execId") stringExecId: String
    ) {
        println("failing exec $stringExecId")
    }


}