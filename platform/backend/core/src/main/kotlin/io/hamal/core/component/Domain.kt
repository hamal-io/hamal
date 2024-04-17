package io.hamal.core.component

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeGenerator
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.GenerateCmdId
import io.hamal.lib.domain.GenerateDomainId

class GenerateDomainIdImpl(private val generator: SnowflakeGenerator) : GenerateDomainId {
    override fun <ID : ValueObjectId> invoke(ctor: (SnowflakeId) -> ID): ID {
        return ctor(generator.next())
    }
}

class GenerateCmdIdImpl(private val generator: SnowflakeGenerator) : GenerateCmdId {
    override fun invoke(): CmdId {
        return CmdId(generator.next())
    }
}

