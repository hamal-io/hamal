package io.hamal.core.request.handler.topic

import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.domain.request.TopicGroupCreateRequested
import io.hamal.lib.domain.request.TopicPublicCreateRequested
import io.hamal.repository.api.TopicCmdRepository
import io.hamal.repository.api.TopicCmdRepository.TopicGroupCreateCmd
import io.hamal.repository.api.TopicCmdRepository.TopicPublicCreateCmd
import org.springframework.stereotype.Component

@Component
class TopicGroupCreateHandler(
    private val topicRepository: TopicCmdRepository
) : RequestHandler<TopicGroupCreateRequested>(TopicGroupCreateRequested::class) {
    override fun invoke(req: TopicGroupCreateRequested) {
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

@Component
class TopicPublicCreateHandler(
    private val topicRepository: TopicCmdRepository
) : RequestHandler<TopicPublicCreateRequested>(TopicPublicCreateRequested::class) {
    override fun invoke(req: TopicPublicCreateRequested) {
        topicRepository.create(
            TopicPublicCreateCmd(
                id = req.cmdId(),
                topicId = req.topicId,
                logTopicId = req.logTopicId,
                name = req.name,
                groupId = req.groupId
            )
        )
    }
}