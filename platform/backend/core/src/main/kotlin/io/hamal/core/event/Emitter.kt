package io.hamal.core.event

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.api.TopicRepository
import io.hamal.repository.api.event.InternalEvent
import io.hamal.repository.api.new_log.LogBrokerRepository
import io.hamal.repository.api.new_log.LogTopicAppenderImpl

class InternalEventEmitter(
    private val topicRepository: TopicRepository,
    logTopicRepository: LogBrokerRepository
) {

    fun <EVENT : InternalEvent> emit(cmdId: CmdId, evt: EVENT) {
        val topic = topicRepository.getGroupTopic(GroupId.root, evt.topicName)
        appender.append(cmdId, topic.logTopicId, evt)
    }

    private val appender = LogTopicAppenderImpl<InternalEvent>(logTopicRepository)
}
