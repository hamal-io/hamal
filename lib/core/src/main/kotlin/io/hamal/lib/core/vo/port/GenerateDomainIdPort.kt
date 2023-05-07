package io.hamal.lib.core.vo.port

import io.hamal.lib.core.KeyedOnce
import io.hamal.lib.core.Shard
import io.hamal.lib.core.vo.base.DomainId

interface GenerateDomainIdPort {
    operator fun <ID : DomainId> invoke(shard: Shard, ctor: (io.hamal.lib.core.util.SnowflakeId) -> ID): ID
}

object DomainIdGeneratorAdapter : GenerateDomainIdPort {

    private val provideGenerator = KeyedOnce.default<Shard, io.hamal.lib.core.util.SnowflakeId.Generator>()
    override fun <ID : DomainId> invoke(shard: Shard, ctor: (io.hamal.lib.core.util.SnowflakeId) -> ID): ID {
        val generator = DomainIdGeneratorAdapter.provideGenerator(shard) {
            io.hamal.lib.core.util.SnowflakeGenerator(
                io.hamal.lib.core.util.DefaultPartitionSource(shard.value)
            )
        }
        return ctor(generator.next())
    }
}

class FixedTimeIdGeneratorAdapter : GenerateDomainIdPort {
    private val provideGenerator = KeyedOnce.default<Shard, io.hamal.lib.core.util.SnowflakeId.Generator>()
    override fun <ID : DomainId> invoke(shard: Shard, ctor: (io.hamal.lib.core.util.SnowflakeId) -> ID): ID {
        val generator = provideGenerator(shard) {
            io.hamal.lib.core.util.SnowflakeGenerator(
                elapsedSource = io.hamal.lib.core.util.FixedElapsedSource(0),
                partitionSource = io.hamal.lib.core.util.DefaultPartitionSource(shard.value),
            )
        }
        return ctor(generator.next())
    }
}