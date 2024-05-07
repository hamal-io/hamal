package io.hamal.core.component

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.TopicTypes.Internal
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicType.Companion.TopicType
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.repository.api.TopicCmdRepository
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
            topicRepository.findTopic(NamespaceId.root, topicName) ?: run {
                topicRepository.create(
                    generateDomainId(::TopicId).let { topicId ->
                        TopicCmdRepository.TopicCreateCmd(
                            id = CmdId(ValueSnowflakeId(SnowflakeId(topicId.longValue))),
                            topicId = topicId,
                            name = topicName,
                            logTopicId = generateDomainId(::LogTopicId),
                            workspaceId = WorkspaceId.root,
                            namespaceId = NamespaceId.root,
                            type = TopicType(Internal)
                        )
                    }
                )
            }
        }
    }

    @Autowired
    private lateinit var topicRepository: TopicRepository

    @Autowired
    private lateinit var generateDomainId: GenerateDomainId
}