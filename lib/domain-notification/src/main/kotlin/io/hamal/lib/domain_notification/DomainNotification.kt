package io.hamal.lib.domain_notification

import io.hamal.lib.domain.vo.base.RegionId

interface DomainNotification {
    val regionId: RegionId
    fun topic() = this::class.topic()
}