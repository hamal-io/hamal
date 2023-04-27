package io.hamal.lib.vo.port

import io.hamal.lib.vo.RegionId


fun interface ResolveRegionPort {
    operator fun invoke(): RegionId
}