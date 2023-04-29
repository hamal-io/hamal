package io.hamal.lib.vo.port

import io.hamal.lib.vo.Shard


fun interface ResolveRegionPort {
    operator fun invoke(): Shard
}