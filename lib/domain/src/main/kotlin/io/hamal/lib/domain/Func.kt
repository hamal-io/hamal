package io.hamal.lib.domain

import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class Func(
    override val id: FuncId,
    val cmdId: CmdId,
    val name: FuncName,
    val inputs: FuncInputs,
    val secrets: FuncSecrets,
    val code: Code
) : DomainObject<FuncId>