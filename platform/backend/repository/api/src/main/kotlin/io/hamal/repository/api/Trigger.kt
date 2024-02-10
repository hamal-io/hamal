package io.hamal.repository.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.domain.*
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.TriggerStatus
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import java.lang.reflect.Type

interface TriggerRepository : TriggerCmdRepository, TriggerQueryRepository

interface TriggerCmdRepository : CmdRepository {
    fun create(cmd: CreateFixedRateCmd): FixedRateTrigger
    fun create(cmd: CreateEventCmd): EventTrigger
    fun create(cmd: CreateHookCmd): HookTrigger
    fun create(cmd: CreateCronCmd): CronTrigger
    fun set(triggerId: TriggerId, cmd: SetTriggerStatusCmd): Trigger

    data class CreateFixedRateCmd(
        val id: CmdId,
        val triggerId: TriggerId,
        val groupId: GroupId,
        val name: TriggerName,
        val funcId: FuncId,
        val namespaceId: NamespaceId,
        val inputs: TriggerInputs,
        val duration: TriggerDuration,
        val correlationId: CorrelationId? = null,
        val status: TriggerStatus = TriggerStatus.Active
    )

    data class CreateEventCmd(
        val id: CmdId,
        val triggerId: TriggerId,
        val groupId: GroupId,
        val name: TriggerName,
        val funcId: FuncId,
        val namespaceId: NamespaceId,
        val inputs: TriggerInputs,
        val topicId: TopicId,
        val correlationId: CorrelationId? = null,
        val status: TriggerStatus = TriggerStatus.Active
    )

    data class CreateHookCmd(
        val id: CmdId,
        val triggerId: TriggerId,
        val groupId: GroupId,
        val name: TriggerName,
        val funcId: FuncId,
        val namespaceId: NamespaceId,
        val inputs: TriggerInputs,
        val hookId: HookId,
        val hookMethod: HookMethod,
        val correlationId: CorrelationId? = null,
        val status: TriggerStatus = TriggerStatus.Active
    )

    data class CreateCronCmd(
        val id: CmdId,
        val triggerId: TriggerId,
        val groupId: GroupId,
        val name: TriggerName,
        val funcId: FuncId,
        val namespaceId: NamespaceId,
        val inputs: TriggerInputs,
        val cron: CronPattern,
        val correlationId: CorrelationId? = null,
        val status: TriggerStatus = TriggerStatus.Active
    )

    data class SetTriggerStatusCmd(
        val id: CmdId,
        val status: TriggerStatus
    )
}

interface TriggerQueryRepository {
    fun get(triggerId: TriggerId) = find(triggerId) ?: throw NoSuchElementException("Trigger not found")
    fun find(triggerId: TriggerId): Trigger?
    fun list(query: TriggerQuery): List<Trigger>
    fun count(query: TriggerQuery): Count

    data class TriggerQuery(
        var afterId: TriggerId = TriggerId(SnowflakeId(Long.MAX_VALUE)),
        var types: List<TriggerType> = TriggerType.values().toList(),
        var limit: Limit = Limit(1),
        var triggerIds: List<TriggerId> = listOf(),
        var funcIds: List<FuncId> = listOf(),
        var topicIds: List<TopicId> = listOf(),
        var hookIds: List<HookId> = listOf(),
        var groupIds: List<GroupId> = listOf(),
        var namespaceIds: List<NamespaceId> = listOf()
    )
}

sealed interface Trigger : DomainObject<TriggerId> {
    val cmdId: CmdId
    val groupId: GroupId
    val name: TriggerName
    val funcId: FuncId
    val namespaceId: NamespaceId
    val correlationId: CorrelationId?
    val inputs: TriggerInputs
    val type: TriggerType
    val status: TriggerStatus

    object Adapter : JsonAdapter<Trigger> {
        override fun serialize(
            src: Trigger,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            return context.serialize(src)
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): Trigger {
            val type = json.asJsonObject.get("type").asString
            return context.deserialize(json, classMapping[type]!!.java)
        }

        private val classMapping = mapOf(
            TriggerType.FixedRate.name to FixedRateTrigger::class,
            TriggerType.Event.name to EventTrigger::class,
            TriggerType.Hook.name to HookTrigger::class,
            TriggerType.Cron.name to CronTrigger::class
        )
    }
}

class FixedRateTrigger(
    override val cmdId: CmdId,
    override val id: TriggerId,
    override val updatedAt: UpdatedAt,
    override val groupId: GroupId,
    override val name: TriggerName,
    override val funcId: FuncId,
    override val namespaceId: NamespaceId,
    override val inputs: TriggerInputs,
    override val status: TriggerStatus,
    val duration: TriggerDuration,
    override val correlationId: CorrelationId? = null
) : Trigger {
    override val type = TriggerType.FixedRate
}

class EventTrigger(
    override val cmdId: CmdId,
    override val id: TriggerId,
    override val updatedAt: UpdatedAt,
    override val groupId: GroupId,
    override val name: TriggerName,
    override val funcId: FuncId,
    override val namespaceId: NamespaceId,
    override val inputs: TriggerInputs,
    override val status: TriggerStatus,
    val topicId: TopicId,
    override val correlationId: CorrelationId? = null
) : Trigger {
    override val type = TriggerType.Event
}

class HookTrigger(
    override val cmdId: CmdId,
    override val id: TriggerId,
    override val updatedAt: UpdatedAt,
    override val groupId: GroupId,
    override val name: TriggerName,
    override val funcId: FuncId,
    override val namespaceId: NamespaceId,
    override val inputs: TriggerInputs,
    override val status: TriggerStatus,
    override val correlationId: CorrelationId? = null,
    val hookId: HookId,
    val hookMethod: HookMethod
) : Trigger {
    override val type = TriggerType.Hook
}

class CronTrigger(
    override val cmdId: CmdId,
    override val id: TriggerId,
    override val updatedAt: UpdatedAt,
    override val groupId: GroupId,
    override val name: TriggerName,
    override val funcId: FuncId,
    override val namespaceId: NamespaceId,
    override val inputs: TriggerInputs,
    override val status: TriggerStatus,
    override val correlationId: CorrelationId? = null,
    val cron: CronPattern
) : Trigger {
    override val type = TriggerType.Cron
}
