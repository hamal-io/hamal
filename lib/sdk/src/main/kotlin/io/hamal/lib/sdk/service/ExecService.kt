package io.hamal.lib.sdk.service

import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.domain.ApiExecutions

interface ExecService {
    fun poll(): ApiExecutions

    fun complete(execId: ExecId)

    fun fail(execId: ExecId)
}

class DefaultExecService : ExecService {
    override fun poll(): ApiExecutions {
        return HttpTemplate("http://localhost:8084")
            .post("/v1/dequeue")
            .execute(ApiExecutions::class)
    }

    override fun complete(execId: ExecId) {
        HttpTemplate("http://localhost:8084")
            .post("/v1/execs/${execId.value.value}/complete")
            .execute()
    }

    override fun fail(execId: ExecId) {
        HttpTemplate("http://localhost:8084")
            .post("/v1/execs/${execId.value.value}/fail")
            .execute()
    }

}