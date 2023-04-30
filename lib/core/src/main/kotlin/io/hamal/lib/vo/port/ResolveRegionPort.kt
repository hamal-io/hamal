package io.hamal.lib.vo.port

import io.hamal.lib.Shard


fun interface ResolveRegionPort {
    operator fun invoke(): Shard
}