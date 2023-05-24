package io.hamal.backend.infra.notification

import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.FuncId
import kotlinx.serialization.Serializable

@Serializable
@DomainNotificationTopic("func::created")
data class FuncCreatedNotification(
    override val shard: Shard,
    val id: FuncId,
) : DomainNotification()
