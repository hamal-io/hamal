package io.hamal.lib.domain.vo.port

import io.hamal.lib.domain.vo.RegionId

fun interface ResolveRegionPort {
    operator fun invoke() : RegionId
}