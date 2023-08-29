package io.hamal.lib.sdk.service

import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.domain.AppendExecLogCmd

interface ExecLogService {
    fun append(execId: ExecId, cmd: AppendExecLogCmd)
}

class DefaultExecLogService(val template: HttpTemplate) : ExecLogService {

    override fun append(execId: ExecId, cmd: AppendExecLogCmd) {
        template
            .post("/v1/execs/{execId}/logs")
            .path("execId", execId)
            .body(cmd)
            .execute()
    }
}