package io.hamal.lib.core.vo.port

import io.hamal.lib.core.Shard


fun interface ResolveRegionPort {
    operator fun invoke(): Shard
}