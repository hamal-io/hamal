package io.hamal.backend.repository.api

import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.Func
import io.hamal.lib.domain.vo.*


interface FuncCmdRepository {
    fun create(cmd: CreateCmd): Func
    data class CreateCmd(
        val id: CmdId,
        val funcId: FuncId,
        val name: FuncName,
        val inputs: FuncInputs,
        val secrets: FuncSecrets,
        val code: Code,
    )
}

interface FuncQueryRepository {
    fun find(funcId: FuncId): Func?

    fun list(afterId: FuncId, limit: Int): List<Func>
}