package io.hamal.lib.domain.vo.port

import io.hamal.lib.domain.vo.base.DomainId
import io.hamal.lib.domain.vo.RegionId
import io.hamal.lib.util.Snowflake

interface GenerateDomainIdPort {
    operator fun <ID : DomainId> invoke(regionId: RegionId, ctor: (Snowflake.Id) -> ID): ID
}