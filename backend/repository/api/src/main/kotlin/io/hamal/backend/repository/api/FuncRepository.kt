package io.hamal.backend.repository.api

import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.Func
import io.hamal.lib.domain.vo.*


interface FuncCmdRepository {
    fun create(cmd: CreateCmd): Func
    fun clear()
    data class CreateCmd(
        val id: CmdId,
        val funcId: FuncId,
        val name: FuncName,
        val inputs: FuncInputs,
        val code: Code,
    )
}

interface FuncQueryRepository {
    fun get(funcId: FuncId) = find(funcId) ?: throw NoSuchElementException("Func not found")
    fun find(funcId: FuncId): Func?
    fun list(block: FuncQuery.() -> Unit): List<Func>
    data class FuncQuery(
        var afterId: FuncId = FuncId(0),
        var limit: Limit = Limit(1)
    )
}