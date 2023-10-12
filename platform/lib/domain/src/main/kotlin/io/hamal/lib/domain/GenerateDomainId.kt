package io.hamal.lib.domain

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.common.Partition
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.snowflake.PartitionSourceImpl
import io.hamal.lib.common.snowflake.SnowflakeGenerator
import io.hamal.lib.common.snowflake.SnowflakeId

interface GenerateDomainId {
    operator fun <ID : DomainId> invoke(ctor: (SnowflakeId) -> ID): ID
}

class DomainIdGeneratorImpl(
    private val partition: Partition
) : GenerateDomainId {
    private val provideGenerator = KeyedOnce.default<(SnowflakeId) -> DomainId, SnowflakeId.Generator>()
    override fun <ID : DomainId> invoke(ctor: (SnowflakeId) -> ID): ID {
        val generator = provideGenerator(ctor) {
            SnowflakeGenerator(PartitionSourceImpl(partition.value))
        }
        return ctor(generator.next())
    }
}