package io.hamal.lib.domain.vo.port

import io.hamal.lib.domain.Shard


fun interface ResolveRegionPort {
    operator fun invoke(): Shard
}