package io.hamal.core.request.handler.exec

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain.State
import io.hamal.lib.domain.request.ExecCompleteRequested
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.*
import io.hamal.repository.api.event.ExecutionCompletedEvent
import io.hamal.repository.api.log.AppenderImpl
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.CreateTopic.TopicToCreate
import org.springframework.stereotype.Component


@Component
class ExecCompleteHandler(
    private val execQueryRepository: ExecQueryRepository,
    private val execCmdRepository: ExecCmdRepository,
    private val eventEmitter: PlatformEventEmitter,
    private val stateCmdRepository: StateCmdRepository,
    private val eventBrokerRepository: BrokerRepository,
    private val generateDomainId: GenerateId,
    private val flowQueryRepository: FlowQueryRepository
) : io.hamal.core.request.RequestHandler<ExecCompleteRequested>(ExecCompleteRequested::class) {

    override fun invoke(req: ExecCompleteRequested) {
        val cmdId = req.cmdId()

        val exec = execQueryRepository.get(req.execId)
        require(exec is StartedExec) { "Exec not in status Started" }

        val flowId = exec.flowId

        completeExec(req)
            .also { emitCompletionEvent(cmdId, it) }
            .also { setState(cmdId, it) }
            .also { appendEvents(cmdId, flowId, req.events) }
    }

    private fun completeExec(req: ExecCompleteRequested) =
        execCmdRepository.complete(
            ExecCmdRepository.CompleteCmd(
                req.cmdId(),
                req.execId,
                req.result,
                ExecState(req.state.value)
            )
        )

    private fun emitCompletionEvent(cmdId: CmdId, exec: CompletedExec) {
        eventEmitter.emit(cmdId, ExecutionCompletedEvent(exec))
    }

    private fun setState(cmdId: CmdId, exec: CompletedExec) {
        val correlation = exec.correlation
        if (correlation != null) {
            stateCmdRepository.set(
                cmdId, CorrelatedState(
                    correlation = correlation,
                    value = State(exec.state.value)
                )
            )
        }
    }

    private fun appendEvents(cmdId: CmdId, flowId: FlowId, events: List<EventToSubmit>) {
        events.forEach { evt ->
            //FIXME create topic if not exists
            val topicName = evt.topicName
            val flow = flowQueryRepository.get(flowId)
            val topic = eventBrokerRepository.findTopic(flowId, topicName) ?: eventBrokerRepository.create(
                cmdId, TopicToCreate(
                    id = generateDomainId(::TopicId),
                    name = topicName,
                    flowId = flowId,
                    groupId = flow.groupId
                )
            )
            appender.append(cmdId, topic, TopicEntryPayload(evt.payload.value))
        }
    }

    private val appender = AppenderImpl<TopicEntryPayload>(eventBrokerRepository)

}
