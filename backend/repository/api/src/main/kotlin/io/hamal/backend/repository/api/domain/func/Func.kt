package io.hamal.backend.repository.api.domain.func

import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncName
import kotlinx.serialization.Serializable

@Serializable
data class Func(
    override val id: FuncId,
    val name: FuncName,
    val code: Code
) : DomainObject<FuncId>