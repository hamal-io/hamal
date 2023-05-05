package io.hamal.lib.vo.port

import io.hamal.lib.KeyedOnce
import io.hamal.lib.Shard
import io.hamal.lib.util.DefaultPartitionSource
import io.hamal.lib.util.FixedElapsedSource
import io.hamal.lib.util.SnowflakeGenerator
import io.hamal.lib.util.SnowflakeId
import io.hamal.lib.vo.base.DomainId

interface GenerateDomainIdPort {
    operator fun <ID : DomainId> invoke(shard: Shard, ctor: (SnowflakeId) -> ID): ID
}

object DomainIdGeneratorAdapter : GenerateDomainIdPort {

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

class FixedTimeIdGeneratorAdapter : GenerateDomainIdPort {
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