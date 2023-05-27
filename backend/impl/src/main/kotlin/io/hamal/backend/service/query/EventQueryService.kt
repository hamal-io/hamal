package io.hamal.backend.service.query

import io.hamal.backend.repository.api.log.BrokerRepository
import io.hamal.backend.repository.api.log.Topic
import io.hamal.lib.domain.vo.TopicId
import org.springframework.stereotype.Service

@Service
class EventQueryService(
    val brokerRepository: BrokerRepository
) {

    fun query(block: TopicQuery.() -> Unit): List<Topic> {
        val query = TopicQuery()
        block(query)
        //FIXMEs
        return brokerRepository.topics().toList()
    }


    data class TopicQuery(
        val afterId: TopicId = TopicId(0),
        val limit: Int = 100 //FIXME VO
    )
}