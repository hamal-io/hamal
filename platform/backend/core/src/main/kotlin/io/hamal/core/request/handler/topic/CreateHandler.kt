package io.hamal.core.request.handler.topic

import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.domain.request.TopicFlowCreateRequested
import io.hamal.repository.api.TopicCmdRepository
import io.hamal.repository.api.TopicCmdRepository.TopicGroupCreateCmd
import org.springframework.stereotype.Component

@Component
class TopicFlowCreateHandler(
    private val topicRepository: TopicCmdRepository
) : RequestHandler<TopicFlowCreateRequested>(TopicFlowCreateRequested::class) {
    override fun invoke(req: TopicFlowCreateRequested) {
        topicRepository.create(
            TopicGroupCreateCmd(
                id = req.cmdId(),
                topicId = req.topicId,
                logTopicId = req.logTopicId,
                name = req.name,
                groupId = req.groupId
            )
        )
    }
}