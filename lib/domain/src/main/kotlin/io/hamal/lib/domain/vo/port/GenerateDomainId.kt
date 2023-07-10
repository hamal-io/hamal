package io.hamal.lib.domain.vo.port

import io.hamal.lib.common.*
import io.hamal.lib.common.domain.DomainId

interface GenerateDomainId {
    operator fun <ID : DomainId> invoke(ctor: (SnowflakeId) -> ID): ID
}

class DefaultDomainIdGenerator(
    private val partition: Partition
) : GenerateDomainId {
    private val provideGenerator = KeyedOnce.default<(SnowflakeId) -> DomainId, SnowflakeId.Generator>()
    override fun <ID : DomainId> invoke(ctor: (SnowflakeId) -> ID): ID {
        val generator = provideGenerator(ctor) {
            SnowflakeGenerator(
                DefaultPartitionSource(partition.value)
            )
        }
        return ctor(generator.next())
    }
}