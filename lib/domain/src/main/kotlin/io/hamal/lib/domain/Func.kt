package io.hamal.lib.domain

import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import kotlinx.serialization.Serializable

@Serializable
data class Func(
    override val id: FuncId,
    val cmdId: CmdId,
    val name: FuncName,
    val inputs: FuncInputs,
    val code: Code
) : DomainObject<FuncId>