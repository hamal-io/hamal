package io.hamal.core.request.handler.topic

import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.domain.request.TopicCreateRequested
import io.hamal.repository.api.TopicCmdRepository
import org.springframework.stereotype.Component

@Component
class TopicCreateHandler(
    private val topicRepository: TopicCmdRepository
) : RequestHandler<TopicCreateRequested>(TopicCreateRequested::class) {
    override fun invoke(req: TopicCreateRequested) {
        topicRepository.create(
            TopicCmdRepository.TopicCreateCmd(
                id = req.cmdId(),
                topicId = req.topicId,
                logTopicId = req.logTopicId,
                name = req.name,
                groupId = req.groupId,
                namespaceId = req.namespaceId,
                type = req.type
            )
        )
    }
}
