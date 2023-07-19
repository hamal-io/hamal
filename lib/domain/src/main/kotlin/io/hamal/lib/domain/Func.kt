package io.hamal.lib.domain

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.kua.value.CodeValue
import kotlinx.serialization.Serializable

@Serializable
data class Func(
    override val id: FuncId,
    val cmdId: CmdId,
    val name: FuncName,
    val inputs: FuncInputs,
    val code: CodeValue
) : DomainObject<FuncId>