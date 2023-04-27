package io.hamal.bootstrap.adapter

import io.hamal.lib.vo.RegionId
import io.hamal.lib.vo.base.DomainId
import io.hamal.lib.vo.port.GenerateDomainIdPort
import io.hamal.lib.util.DefaultPartitionSource
import io.hamal.lib.util.Snowflake
import io.hamal.lib.util.SnowflakeGenerator

object DomainIdGeneratorAdapter : GenerateDomainIdPort {

    private val provideGenerator = io.hamal.lib.KeyedOnce.default<RegionId, Snowflake.Generator>()
    override fun <ID : DomainId> invoke(regionId: RegionId, ctor: (Snowflake.Id) -> ID): ID {
        val generator = provideGenerator(regionId) {
            SnowflakeGenerator(
                DefaultPartitionSource(regionId.value)
            )
        }
        return ctor(generator.next())
    }
}