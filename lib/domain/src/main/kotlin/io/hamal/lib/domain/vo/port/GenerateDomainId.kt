package io.hamal.lib.domain.vo.port

import io.hamal.lib.common.*
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.base.DomainId

interface GenerateDomainId {
    operator fun <ID : DomainId> invoke(shard: Shard, ctor: (SnowflakeId) -> ID): ID
}

object DomainIdGeneratorAdapter : GenerateDomainId {

    private val provideGenerator = KeyedOnce.default<Shard, SnowflakeId.Generator>()
    override fun <ID : DomainId> invoke(shard: Shard, ctor: (SnowflakeId) -> ID): ID {
        val generator = provideGenerator(shard) {
            SnowflakeGenerator(
                DefaultPartitionSource(shard.value)
            )
        }
        return ctor(generator.next())
    }
}

class FixedTimeIdGeneratorAdapter : GenerateDomainId {
    private val provideGenerator = KeyedOnce.default<Shard, SnowflakeId.Generator>()
    override fun <ID : DomainId> invoke(shard: Shard, ctor: (SnowflakeId) -> ID): ID {
        val generator = provideGenerator(shard) {
            SnowflakeGenerator(
                elapsedSource = FixedElapsedSource(0),
                partitionSource = DefaultPartitionSource(shard.value),
            )
        }
        return ctor(generator.next())
    }
}