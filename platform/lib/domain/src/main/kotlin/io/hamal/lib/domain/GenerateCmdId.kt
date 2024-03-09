package io.hamal.lib.domain

import io.hamal.lib.common.Partition
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.snowflake.PartitionSourceImpl
import io.hamal.lib.common.snowflake.SnowflakeGenerator

interface GenerateCmdId {
    operator fun invoke(): CmdId
}

class CmdIdGeneratorImpl(
    private val partition: Partition
) : GenerateCmdId {

    override fun invoke(): CmdId {
        return CmdId(generator.next())
    }

    private val generator = SnowflakeGenerator(PartitionSourceImpl(partition.value))
}