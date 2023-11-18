package io.hamal.repository.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.domain.UpdatedAt
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable
import kotlin.time.Duration

interface TriggerRepository : TriggerCmdRepository, TriggerQueryRepository

interface TriggerCmdRepository : CmdRepository {
    fun create(cmd: CreateFixedRateCmd): FixedRateTrigger
    fun create(cmd: CreateEventCmd): EventTrigger
    fun create(cmd: CreateHookCmd): HookTrigger
    fun create(cmd: CreateCronCmd): CronTrigger

    data class CreateFixedRateCmd(
        val id: CmdId,
        val triggerId: TriggerId,
        val groupId: GroupId,
        val name: TriggerName,
        val funcId: FuncId,
        val flowId: FlowId,
        val inputs: TriggerInputs,
        val duration: Duration,
        val correlationId: CorrelationId? = null
    )

    data class CreateEventCmd(
        val id: CmdId,
        val triggerId: TriggerId,
        val groupId: GroupId,
        val name: TriggerName,
        val funcId: FuncId,
        val flowId: FlowId,
        val inputs: TriggerInputs,
        val topicId: TopicId,
        val correlationId: CorrelationId? = null
    )

    data class CreateHookCmd(
        val id: CmdId,
        val triggerId: TriggerId,
        val groupId: GroupId,
        val name: TriggerName,
        val funcId: FuncId,
        val flowId: FlowId,
        val inputs: TriggerInputs,
        val hookId: HookId,
        val hookMethods: Set<HookMethod>,
        val correlationId: CorrelationId? = null
    )

    data class CreateCronCmd(
        val id: CmdId,
        val triggerId: TriggerId,
        val groupId: GroupId,
        val name: TriggerName,
        val funcId: FuncId,
        val flowId: FlowId,
        val inputs: TriggerInputs,
        val cron: CronPattern,
        val correlationId: CorrelationId? = null
    )
}

interface TriggerQueryRepository {
    fun get(triggerId: TriggerId) = find(triggerId) ?: throw NoSuchElementException("Trigger not found")
    fun find(triggerId: TriggerId): Trigger?

    fun list(query: TriggerQuery): List<Trigger>
    fun count(query: TriggerQuery): ULong

    data class TriggerQuery(
        var afterId: TriggerId = TriggerId(SnowflakeId(Long.MAX_VALUE)),
        var types: List<TriggerType> = TriggerType.values().toList(),
        var limit: Limit = Limit(1),
        var triggerIds: List<TriggerId> = listOf(),
        var funcIds: List<FuncId> = listOf(),
        var topicIds: List<TopicId> = listOf(),
        var hookIds: List<HookId> = listOf(),
        var groupIds: List<GroupId> = listOf(),
        var flowIds: List<FlowId> = listOf()
    )
}


@Serializable
sealed interface Trigger : DomainObject<TriggerId> {
    val cmdId: CmdId
    val groupId: GroupId
    val name: TriggerName
    val funcId: FuncId
    val flowId: FlowId
    val correlationId: CorrelationId?
    val inputs: TriggerInputs
    val type: TriggerType
}

@Serializable
class FixedRateTrigger(
    override val cmdId: CmdId,
    override val id: TriggerId,
    override val updatedAt: UpdatedAt,
    override val groupId: GroupId,
    override val name: TriggerName,
    override val funcId: FuncId,
    override val flowId: FlowId,
    override val inputs: TriggerInputs,
    val duration: Duration,
    override val correlationId: CorrelationId? = null
) : Trigger {
    override val type = TriggerType.FixedRate
}

@Serializable
class EventTrigger(
    override val cmdId: CmdId,
    override val id: TriggerId,
    override val updatedAt: UpdatedAt,
    override val groupId: GroupId,
    override val name: TriggerName,
    override val funcId: FuncId,
    override val flowId: FlowId,
    override val inputs: TriggerInputs,
    val topicId: TopicId,
    override val correlationId: CorrelationId? = null
) : Trigger {
    override val type = TriggerType.Event
}


@Serializable
class HookTrigger(
    override val cmdId: CmdId,
    override val id: TriggerId,
    override val updatedAt: UpdatedAt,
    override val groupId: GroupId,
    override val name: TriggerName,
    override val funcId: FuncId,
    override val flowId: FlowId,
    override val inputs: TriggerInputs,
    override val correlationId: CorrelationId? = null,
    val hookId: HookId,
    val hookMethods: Set<HookMethod>
) : Trigger {
    override val type = TriggerType.Hook
}

@Serializable
class CronTrigger(
    override val cmdId: CmdId,
    override val id: TriggerId,
    override val updatedAt: UpdatedAt,
    override val groupId: GroupId,
    override val name: TriggerName,
    override val funcId: FuncId,
    override val flowId: FlowId,
    override val inputs: TriggerInputs,
    override val correlationId: CorrelationId? = null,
    val cron: CronPattern
) : Trigger {
    override val type = TriggerType.Cron
}
