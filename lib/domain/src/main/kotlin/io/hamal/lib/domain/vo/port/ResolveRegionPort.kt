package io.hamal.lib.domain.vo.port

import io.hamal.lib.common.Partition


fun interface ResolveRegionPort {
    operator fun invoke(): Partition
}