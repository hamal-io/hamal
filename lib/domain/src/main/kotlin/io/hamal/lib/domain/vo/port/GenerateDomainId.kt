package io.hamal.lib.domain.vo.port

import io.hamal.lib.common.DefaultPartitionSource
import io.hamal.lib.common.Partition
import io.hamal.lib.common.SnowflakeGenerator
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.base.DomainId

interface GenerateDomainId {
    operator fun <ID : DomainId> invoke(ctor: (SnowflakeId) -> ID): ID
}

class DefaultDomainIdGenerator(
    partition: Partition
) : GenerateDomainId {
    override fun <ID : DomainId> invoke(ctor: (SnowflakeId) -> ID): ID {
        return ctor(generator.next())
    }

    private val generator = SnowflakeGenerator(
        partitionSource = DefaultPartitionSource(partition.value)
    )
}
