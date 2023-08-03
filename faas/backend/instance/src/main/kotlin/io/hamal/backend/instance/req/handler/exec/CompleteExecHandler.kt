package io.hamal.backend.instance.req.handler.exec

import io.hamal.backend.instance.event.ExecutionCompletedEvent
import io.hamal.backend.instance.event.SystemEventEmitter
import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.repository.api.StateCmdRepository
import io.hamal.backend.repository.api.log.CreateTopic.TopicToCreate
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.api.log.LogTopic
import io.hamal.backend.repository.api.log.ProtobufAppender
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.*
import io.hamal.lib.domain.req.SubmittedCompleteExecReq
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.domain.vo.port.GenerateDomainId
import io.hamal.lib.kua.value.StringValue
import org.springframework.stereotype.Component

@Component
class CompleteExecHandler<TOPIC : LogTopic>(
    private val execQueryRepository: ExecQueryRepository,
    private val execCmdRepository: ExecCmdRepository,
    private val eventEmitter: SystemEventEmitter<*>,
    private val stateCmdRepository: StateCmdRepository,
    private val eventBrokerRepository: LogBrokerRepository<TOPIC>,
    private val generateDomainId: GenerateDomainId
) : ReqHandler<SubmittedCompleteExecReq>(SubmittedCompleteExecReq::class) {

    override fun invoke(req: SubmittedCompleteExecReq) {
        val cmdId = req.cmdId()

        val exec = execQueryRepository.get(req.execId)
        require(exec is StartedExec) { "Exec not in status Started" }

        completeExec(req)
            .also { emitCompletionEvent(cmdId, it) }
            .also { setState(cmdId, it, req.state) }
            .also { appendEvents(cmdId, req.events) }

    }

    private fun completeExec(req: SubmittedCompleteExecReq) =
        execCmdRepository.complete(ExecCmdRepository.CompleteCmd(req.cmdId(), req.execId))

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

    private fun appendEvents(cmdId: CmdId, events: List<Event>) {
        events.forEach { evt ->
            //FIXME create topic if not exists
            val topicName = TopicName((evt.value["topic"] as StringValue).value)
            val topic = eventBrokerRepository.findTopic(topicName) ?: eventBrokerRepository.create(
                cmdId, TopicToCreate(
                    id = generateDomainId(::TopicId),
                    name = topicName
                )
            )
            println(topic.id)
            appender.append(cmdId, topic, evt)
        }
    }

    private val appender = ProtobufAppender(Event::class, eventBrokerRepository)

}
