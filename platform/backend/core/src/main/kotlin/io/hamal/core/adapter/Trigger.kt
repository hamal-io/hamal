package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.*
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.Topic
import io.hamal.repository.api.submitted_req.TriggerCreateSubmitted
import io.hamal.request.CreateTriggerReq
import org.springframework.stereotype.Component


interface TriggerCreatePort {
    operator fun <T : Any> invoke(
        flowId: FlowId,
        req: CreateTriggerReq,
        responseHandler: (TriggerCreateSubmitted) -> T
    ): T
}

interface TriggerGetPort {
    operator fun <T : Any> invoke(
        triggerId: TriggerId,
        responseHandler: (Trigger, Func, Flow, Topic?, Hook?) -> T
    ): T
}


interface TriggerListPort {
    operator fun <T : Any> invoke(
        query: TriggerQuery,
        responseHandler: (
            triggers: List<Trigger>,
            funcs: Map<FuncId, Func>,
            flows: Map<FlowId, Flow>,
            topics: Map<TopicId, Topic>,
            hooks: Map<HookId, Hook>
        ) -> T
    ): T
}

interface TriggerPort : TriggerCreatePort, TriggerGetPort, TriggerListPort

@Component
class TriggerAdapter(
    private val eventBrokerRepository: BrokerRepository,
    private val funcQueryRepository: FuncQueryRepository,
    private val generateDomainId: GenerateDomainId,
    private val hookQueryRepository: HookQueryRepository,
    private val flowQueryRepository: FlowQueryRepository,
    private val reqCmdRepository: ReqCmdRepository,
    private val triggerQueryRepository: TriggerQueryRepository,
) : TriggerPort {
    override fun <T : Any> invoke(
        flowId: FlowId,
        req: CreateTriggerReq,
        responseHandler: (TriggerCreateSubmitted) -> T
    ): T {
        ensureFuncExists(req)
        ensureTopicExists(req)
        ensureHookExists(req)

        val flow = flowQueryRepository.get(flowId)
        val func = funcQueryRepository.get(req.funcId)
        return TriggerCreateSubmitted(
            type = req.type,
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            triggerId = generateDomainId(::TriggerId),
            groupId = flow.groupId,
            name = req.name,
            funcId = func.id,
            flowId = flowId,
            inputs = req.inputs,
            correlationId = req.correlationId,
            duration = req.duration,
            topicId = req.topicId,
            hookId = req.hookId,
            hookMethods = req.hookMethods,
            cron = req.cron
        ).also(reqCmdRepository::queue).let(responseHandler)

    }

    override fun <T : Any> invoke(
        triggerId: TriggerId,
        responseHandler: (Trigger, Func, Flow, Topic?, Hook?) -> T
    ): T {
        val trigger = triggerQueryRepository.get(triggerId)
        val func = funcQueryRepository.get(trigger.funcId)
        val flow = flowQueryRepository.get(trigger.flowId)
        val topic = if (trigger is EventTrigger) {
            eventBrokerRepository.getTopic(trigger.topicId)
        } else {
            null
        }

        val hook = if (trigger is HookTrigger) {
            hookQueryRepository.get(trigger.hookId)
        } else {
            null
        }

        return responseHandler(trigger, func, flow, topic, hook)
    }

    override operator fun <T : Any> invoke(
        query: TriggerQuery,
        responseHandler: (
            triggers: List<Trigger>,
            funcs: Map<FuncId, Func>,
            flows: Map<FlowId, Flow>,
            topics: Map<TopicId, Topic>,
            hooks: Map<HookId, Hook>
        ) -> T
    ): T {

        val triggers = triggerQueryRepository.list(query)

        val flows = flowQueryRepository.list(triggers.map { it.flowId })
            .associateBy { it.id }

        val funcs = funcQueryRepository.list(triggers.map { it.funcId })
            .associateBy { it.id }

        val topics = eventBrokerRepository.list(triggers.filterIsInstance<EventTrigger>().map { it.topicId })
            .associateBy { it.id }

        val hooks = hookQueryRepository.list(triggers.filterIsInstance<HookTrigger>().map { it.hookId })
            .associateBy { it.id }

        return responseHandler(triggers, funcs, flows, topics, hooks)
    }

    private fun ensureFuncExists(createTrigger: CreateTriggerReq) {
        funcQueryRepository.get(createTrigger.funcId)
    }

    private fun ensureTopicExists(createTrigger: CreateTriggerReq) {
        if (createTrigger.type == TriggerType.Event) {
            requireNotNull(createTrigger.topicId) { "topicId is missing" }
            eventBrokerRepository.getTopic(createTrigger.topicId!!)
        }
    }

    private fun ensureHookExists(createTrigger: CreateTriggerReq) {
        if (createTrigger.type == TriggerType.Hook) {
            requireNotNull(createTrigger.hookId) { "hookId is missing" }
            hookQueryRepository.get(createTrigger.hookId!!)
        }
    }
}
