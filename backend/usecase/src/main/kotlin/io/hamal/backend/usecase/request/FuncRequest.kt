package io.hamal.backend.usecase.request

import io.hamal.backend.core.func.Func
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.RequestOneUseCase

object FuncRequest {

    data class FuncCreation(
        override val reqId: ReqId,
        override val shard: Shard,

        ) : RequestOneUseCase<Func>

}