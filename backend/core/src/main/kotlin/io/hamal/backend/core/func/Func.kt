package io.hamal.backend.core.func

import io.hamal.backend.core.trigger.Trigger
import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncRef
import kotlinx.serialization.Serializable

@Serializable
data class Func(
    override val id: FuncId,
    val reference: FuncRef,
    val triggers: List<Trigger>,
    val code: Code
) : DomainObject<FuncId>