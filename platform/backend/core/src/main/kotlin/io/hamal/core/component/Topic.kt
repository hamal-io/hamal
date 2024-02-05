package io.hamal.core.component

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.TopicCmdRepository.TopicInternalCreateCmd
import io.hamal.repository.api.TopicRepository
import io.hamal.repository.api.event.internalEventClasses
import io.hamal.repository.api.event.topicName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SetupInternalTopics {

    operator fun invoke() {
        internalEventClasses.forEach { internalEventClass ->
            val topicName = internalEventClass.topicName()
            topicRepository.findGroupTopic(GroupId.root, topicName) ?: topicRepository.create(
                generateDomainId(::TopicId).let { topicId ->
                    TopicInternalCreateCmd(
                        id = CmdId(topicId),
                        topicId = topicId,
                        name = topicName,
                        logTopicId = generateDomainId(::LogTopicId)
                    )
                }
            )
        }
    }

    @Autowired
    private lateinit var topicRepository: TopicRepository

    @Autowired
    private lateinit var generateDomainId: GenerateId
}