package io.hamal.backend.infra.usecase.request

import io.hamal.backend.core.func.Func
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.RequestOneUseCase
import io.hamal.lib.domain.vo.Code

object AdhocRequest {
    data class ExecuteAdhoc(
        override val reqId: ReqId,
        override val shard: Shard,
        val code: Code
    ) : RequestOneUseCase<Func>

}