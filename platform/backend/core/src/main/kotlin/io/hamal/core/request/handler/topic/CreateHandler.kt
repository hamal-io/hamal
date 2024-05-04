package io.hamal.core.request.handler.topic

import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.domain.request.TopicCreateRequested
import io.hamal.lib.domain.vo.TopicType.Companion.TopicType
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
                topicId = req.id,
                logTopicId = req.logTopicId,
                name = req.name,
                workspaceId = req.workspaceId,
                namespaceId = req.namespaceId,
                type = TopicType(req.type)
            )
        )
    }
}
