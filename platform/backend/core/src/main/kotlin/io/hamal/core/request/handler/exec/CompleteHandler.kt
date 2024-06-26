package io.hamal.core.request.handler.exec

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.TopicTypes.Internal
import io.hamal.lib.domain.request.ExecCompleteRequested
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.TopicType.Companion.TopicType
import io.hamal.repository.api.*
import io.hamal.repository.api.event.ExecCompletedEvent
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.api.log.LogTopicAppenderImpl
import org.springframework.stereotype.Component


@Component
class ExecCompleteHandler(
    private val execQueryRepository: ExecQueryRepository,
    private val execCmdRepository: ExecCmdRepository,
    private val eventEmitter: InternalEventEmitter,
    private val stateCmdRepository: StateCmdRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val topicRepository: TopicRepository,
    private val logBrokerRepository: LogBrokerRepository,
    private val generateId: GenerateDomainId
) : RequestHandler<ExecCompleteRequested>(ExecCompleteRequested::class) {

    override fun invoke(req: ExecCompleteRequested) {
        val cmdId = req.cmdId()

        val exec = execQueryRepository.get(req.id)
        require(exec is Exec.Started) { "Exec not in status Started" }

        val namespaceId = exec.namespaceId

        completeExec(req)
            .also { emitCompletionEvent(cmdId, it) }
            .also { setState(cmdId, it) }
            .also { appendEvents(cmdId, namespaceId, req.events) }
    }

    private fun completeExec(req: ExecCompleteRequested) =
        execCmdRepository.complete(
            ExecCmdRepository.CompleteCmd(
                req.cmdId(),
                req.id,
                req.statusCode,
                req.result,
                ExecState(req.state.value)
            )
        )

    private fun emitCompletionEvent(cmdId: CmdId, exec: Exec.Completed) {
        eventEmitter.emit(cmdId, ExecCompletedEvent(exec))
    }

    private fun setState(cmdId: CmdId, exec: Exec.Completed) {
        val correlation = exec.correlation
        if (correlation != null) {
            stateCmdRepository.set(
                StateCmdRepository.SetCmd(
                    id = cmdId,
                    correlatedState = CorrelatedState(
                        correlation = correlation,
                        value = State(exec.state.value)
                    )
                )
            )
        }
    }

    private fun appendEvents(cmdId: CmdId, namespaceId: NamespaceId, events: List<EventToSubmit>) {
        events.forEach { evt ->
            val topicName = evt.topicName
            val namespace = namespaceQueryRepository.get(namespaceId)
            val topic = topicRepository.findTopic(namespace.id, topicName) ?: topicRepository.create(
                TopicCmdRepository.TopicCreateCmd(
                    id = cmdId,
                    topicId = generateId(::TopicId),
                    name = topicName,
                    workspaceId = namespace.workspaceId,
                    namespaceId = namespace.id,
                    logTopicId = generateId(::LogTopicId),
                    type = TopicType(Internal)
                )
            )

            appender.append(cmdId, topic.logTopicId, TopicEventPayload(evt.payload.value))
        }
    }

    private val appender = LogTopicAppenderImpl<TopicEventPayload>(logBrokerRepository)

}
