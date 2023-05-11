package io.hamal.lib.domain.vo.port

import io.hamal.lib.domain.KeyedOnce
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.base.DomainId

interface GenerateDomainIdPort {
    operator fun <ID : DomainId> invoke(shard: Shard, ctor: (io.hamal.lib.domain.util.SnowflakeId) -> ID): ID
}

object DomainIdGeneratorAdapter : GenerateDomainIdPort {

    private val provideGenerator = KeyedOnce.default<io.hamal.lib.domain.Shard, io.hamal.lib.domain.util.SnowflakeId.Generator>()
    override fun <ID : DomainId> invoke(shard: Shard, ctor: (io.hamal.lib.domain.util.SnowflakeId) -> ID): ID {
        val generator = DomainIdGeneratorAdapter.provideGenerator(shard) {
            io.hamal.lib.domain.util.SnowflakeGenerator(
                io.hamal.lib.domain.util.DefaultPartitionSource(shard.value)
            )
        }
        return ctor(generator.next())
    }
}

class FixedTimeIdGeneratorAdapter : GenerateDomainIdPort {
    private val provideGenerator = KeyedOnce.default<io.hamal.lib.domain.Shard, io.hamal.lib.domain.util.SnowflakeId.Generator>()
    override fun <ID : DomainId> invoke(shard: Shard, ctor: (io.hamal.lib.domain.util.SnowflakeId) -> ID): ID {
        val generator = provideGenerator(shard) {
            io.hamal.lib.domain.util.SnowflakeGenerator(
                elapsedSource = io.hamal.lib.domain.util.FixedElapsedSource(0),
                partitionSource = io.hamal.lib.domain.util.DefaultPartitionSource(shard.value),
            )
        }
        return ctor(generator.next())
    }
}