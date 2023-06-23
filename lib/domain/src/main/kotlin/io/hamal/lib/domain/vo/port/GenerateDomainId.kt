package io.hamal.lib.domain.vo.port

import io.hamal.lib.common.*
import io.hamal.lib.domain.vo.base.DomainId

interface GenerateDomainId {
    operator fun <ID : DomainId> invoke(ctor: (SnowflakeId) -> ID): ID
}

class DefaultDomainIdGenerator(
    private val partition: Partition
) : GenerateDomainId {
    private val provideGenerator = KeyedOnce.default<Partition, SnowflakeId.Generator>()
    override fun <ID : DomainId> invoke(ctor: (SnowflakeId) -> ID): ID {
        val generator = provideGenerator(partition) {
            SnowflakeGenerator(
                DefaultPartitionSource(partition.value)
            )
        }
        return ctor(generator.next())
    }
}

class FixedTimeIdGenerator(
    private val partition: Partition
) : GenerateDomainId {
    private val provideGenerator = KeyedOnce.default<Partition, SnowflakeId.Generator>()
    override fun <ID : DomainId> invoke(ctor: (SnowflakeId) -> ID): ID {
        val generator = provideGenerator(partition) {
            SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(0),
                partitionSource = DefaultPartitionSource(partition.value),
            )
        }
        return ctor(generator.next())
    }
}