package io.hamal.lib.domain

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.common.Partition
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.PartitionSourceImpl
import io.hamal.lib.common.snowflake.SnowflakeGenerator
import io.hamal.lib.common.snowflake.SnowflakeId

interface GenerateId {
    operator fun <ID : ValueObjectId> invoke(ctor: (SnowflakeId) -> ID): ID
}

class IdGeneratorImpl(
    private val partition: Partition
) : GenerateId {
    private val provideGenerator = KeyedOnce.default<(SnowflakeId) -> ValueObjectId, SnowflakeId.Generator>()
    override fun <ID : ValueObjectId> invoke(ctor: (SnowflakeId) -> ID): ID {
        val generator = provideGenerator(ctor) {
            SnowflakeGenerator(PartitionSourceImpl(partition.value))
        }
        return ctor(generator.next())
    }
}