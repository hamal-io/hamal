package io.hamal.backend.repository.api

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.Func
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.kua.value.CodeValue


interface FuncCmdRepository {
    fun create(cmd: CreateCmd): Func
    fun clear()
    data class CreateCmd(
        val id: CmdId,
        val funcId: FuncId,
        val name: FuncName,
        val inputs: FuncInputs,
        val code: CodeValue,
    )
}

interface FuncQueryRepository {
    fun get(funcId: FuncId) = find(funcId) ?: throw NoSuchElementException("Func not found")
    fun find(funcId: FuncId): Func?
    fun list(block: FuncQuery.() -> Unit): List<Func>
    data class FuncQuery(
        var afterId: FuncId = FuncId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1)
    )
}