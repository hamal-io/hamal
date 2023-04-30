package io.hamal.bootstrap.adapter

import io.hamal.lib.Shard
import io.hamal.lib.vo.base.DomainId
import io.hamal.lib.vo.port.GenerateDomainIdPort
import io.hamal.lib.util.DefaultPartitionSource
import io.hamal.lib.util.Snowflake
import io.hamal.lib.util.SnowflakeGenerator

object DomainIdGeneratorAdapter : GenerateDomainIdPort {

    private val provideGenerator = io.hamal.lib.KeyedOnce.default<Shard, Snowflake.Generator>()
    override fun <ID : DomainId> invoke(shard: Shard, ctor: (Snowflake.Id) -> ID): ID {
        val generator = provideGenerator(shard) {
            SnowflakeGenerator(
                DefaultPartitionSource(shard.value)
            )
        }
        return ctor(generator.next())
    }
}