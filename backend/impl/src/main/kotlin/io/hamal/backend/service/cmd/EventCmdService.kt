package io.hamal.backend.service.cmd

import io.hamal.backend.repository.api.log.BrokerRepository
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.TenantId
import io.hamal.lib.domain.vo.TopicId
import org.springframework.stereotype.Service

@Service
class EventCmdService(
    val brokerRepository: BrokerRepository
) {

    fun append(eventToAppend: EventToAppend) {
        TODO()
    }

    data class EventToAppend(
        val reqId: ReqId,
        val shard: Shard,

        val tenantId: TenantId,
        val topicId: TopicId,
        val data: String

    )
}