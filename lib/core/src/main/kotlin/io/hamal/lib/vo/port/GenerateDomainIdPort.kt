package io.hamal.lib.vo.port

import io.hamal.lib.util.Snowflake
import io.hamal.lib.vo.Shard
import io.hamal.lib.vo.base.DomainId

interface GenerateDomainIdPort {
    operator fun <ID : DomainId> invoke(shard: Shard, ctor: (Snowflake.Id) -> ID): ID
}