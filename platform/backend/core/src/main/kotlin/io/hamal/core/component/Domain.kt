package io.hamal.core.component

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.snowflake.SnowflakeGenerator
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueVariableSnowflakeId
import io.hamal.lib.domain.GenerateCmdId
import io.hamal.lib.domain.GenerateDomainId

class GenerateDomainIdImpl(private val generator: SnowflakeGenerator) : GenerateDomainId {
    override fun <ID : ValueVariableSnowflakeId> invoke(ctor: (ValueSnowflakeId) -> ID): ID {
        return ctor(ValueSnowflakeId(generator.next()))
    }
}

class GenerateCmdIdImpl(private val generator: SnowflakeGenerator) : GenerateCmdId {
    override fun invoke(): CmdId {
        return CmdId(ValueSnowflakeId(generator.next()))
    }
}

