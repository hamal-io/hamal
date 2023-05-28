package io.hamal.lib.sdk.service

import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.domain.ApiAgentExecRequests

interface ExecService {
    fun poll(): ApiAgentExecRequests

    fun complete(execId: ExecId)

    fun fail(execId: ExecId)
}

class DefaultExecService : ExecService {
    override fun poll(): ApiAgentExecRequests {
        return HttpTemplate("http://localhost:8084")
            .post("/v1/dequeue")
            .execute(ApiAgentExecRequests::class)
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