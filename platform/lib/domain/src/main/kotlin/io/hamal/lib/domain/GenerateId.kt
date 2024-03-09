package io.hamal.lib.domain

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.PartitionSourceImpl
import io.hamal.lib.common.snowflake.SnowflakeGenerator
import io.hamal.lib.common.snowflake.SnowflakeId


interface GenerateDomainId {
    operator fun <ID : ValueObjectId> invoke(ctor: (SnowflakeId) -> ID): ID
}

object IdGeneratorImpl : GenerateDomainId {
    override fun <ID : ValueObjectId> invoke(ctor: (SnowflakeId) -> ID): ID {
        return ctor(generator.next())
    }
}

interface GenerateCmdId {
    operator fun invoke(): CmdId
}

object CmdIdGeneratorImpl : GenerateCmdId {
    override fun invoke(): CmdId {
        return CmdId(generator.next())
    }
}

private val generator = SnowflakeGenerator(PartitionSourceImpl(1))