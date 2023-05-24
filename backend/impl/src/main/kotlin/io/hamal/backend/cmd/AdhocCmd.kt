package io.hamal.backend.cmd

import io.hamal.backend.repository.api.domain.func.Func
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.RequestOneUseCase
import io.hamal.lib.domain.vo.Code

object AdhocCmd {
    data class ExecuteAdhoc(
        override val reqId: ReqId,
        override val shard: Shard,
        val code: Code
    ) : RequestOneUseCase<Func>

}