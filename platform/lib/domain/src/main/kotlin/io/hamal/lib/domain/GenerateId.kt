package io.hamal.lib.domain

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId


interface GenerateDomainId {
    operator fun <ID : ValueObjectId> invoke(ctor: (SnowflakeId) -> ID): ID
}


interface GenerateCmdId {
    operator fun invoke(): CmdId
}


