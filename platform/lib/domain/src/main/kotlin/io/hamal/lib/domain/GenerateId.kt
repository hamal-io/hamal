package io.hamal.lib.domain

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueVariableSnowflakeId


interface GenerateDomainId {
    operator fun <ID : ValueVariableSnowflakeId> invoke(ctor: (ValueSnowflakeId) -> ID): ID
}


interface GenerateCmdId {
    operator fun invoke(): CmdId
}


