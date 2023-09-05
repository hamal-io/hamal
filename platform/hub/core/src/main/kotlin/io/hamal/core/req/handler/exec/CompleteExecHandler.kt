package io.hamal.core.req.handler.exec

import io.hamal.core.event.HubEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.*
import io.hamal.repository.api.event.ExecutionCompletedEvent
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.CreateTopic.TopicToCreate
import io.hamal.repository.api.log.ProtobufAppender
import io.hamal.repository.api.submitted_req.SubmittedCompleteExecReq
import org.springframework.stereotype.Component

@Component
class CompleteExecHandler(
    private val execQueryRepository: ExecQueryRepository,
    private val execCmdRepository: ExecCmdRepository,
    private val eventEmitter: HubEventEmitter,
    private val stateCmdRepository: StateCmdRepository,
    private val eventBrokerRepository: BrokerRepository,
    private val generateDomainId: GenerateDomainId
) : ReqHandler<SubmittedCompleteExecReq>(SubmittedCompleteExecReq::class) {

    override fun invoke(req: SubmittedCompleteExecReq) {
        val cmdId = req.cmdId()

        val exec = execQueryRepository.get(req.id)
        require(exec is StartedExec) { "Exec not in status Started" }

        completeExec(req)
            .also { emitCompletionEvent(cmdId, it) }
            .also { setState(cmdId, it, req.state) }
            .also { appendEvents(cmdId, req.events) }

    }

    private fun completeExec(req: SubmittedCompleteExecReq) =
        execCmdRepository.complete(ExecCmdRepository.CompleteCmd(req.cmdId(), req.id))

    private fun emitCompletionEvent(cmdId: CmdId, exec: CompletedExec) {
        eventEmitter.emit(cmdId, ExecutionCompletedEvent(exec))
    }

    private fun setState(cmdId: CmdId, exec: CompletedExec, state: State) {
        val correlation = exec.correlation
        if (correlation != null) {
            stateCmdRepository.set(
                cmdId, CorrelatedState(
                    correlation = correlation,
                    value = state
                )
            )
        }
    }

    private fun appendEvents(cmdId: CmdId, events: List<EventToSubmit>) {
        events.forEach { evt ->
            //FIXME create topic if not exists
            val topicName = evt.topicName
            val topic = eventBrokerRepository.findTopic(topicName) ?: eventBrokerRepository.create(
                cmdId, TopicToCreate(
                    id = generateDomainId(::TopicId),
                    name = topicName
                )
            )
            appender.append(cmdId, topic, TopicEntryPayload(evt.payload.value))
        }
    }

    private val appender = ProtobufAppender(TopicEntryPayload::class, eventBrokerRepository)

}
