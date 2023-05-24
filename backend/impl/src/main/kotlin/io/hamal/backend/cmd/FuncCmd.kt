package io.hamal.backend.cmd

import io.hamal.backend.repository.api.domain.func.Func
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.RequestOneUseCase
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.FuncName

object FuncCmd {

    data class FuncCreation(
        override val reqId: ReqId,
        override val shard: Shard,
        val name: FuncName,
        val code: Code
    ) : RequestOneUseCase<Func>

}