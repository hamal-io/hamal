package io.hamal.backend.instance.req.handler.event

import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.repository.api.log.CreateTopic
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.api.log.LogTopic
import io.hamal.lib.domain.req.SubmittedCreateTopicReq
import org.springframework.stereotype.Component

@Component
class CreateTopicHandler<TOPIC : LogTopic>(
    private val eventBrokerRepository: LogBrokerRepository<TOPIC>
) : ReqHandler<SubmittedCreateTopicReq>(SubmittedCreateTopicReq::class) {
    override fun invoke(req: SubmittedCreateTopicReq) {
        eventBrokerRepository.create(
            req.cmdId(),
            CreateTopic.TopicToCreate(
                id = req.topicId,
                name = req.name
            )
        )
    }
}