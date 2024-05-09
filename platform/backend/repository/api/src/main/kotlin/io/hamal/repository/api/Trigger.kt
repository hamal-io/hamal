package io.hamal.repository.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.domain.*
import io.hamal.lib.common.domain.Limit.Companion.Limit
import io.hamal.lib.common.serialization.AdapterGeneric
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.TriggerStates.Active
import io.hamal.lib.domain._enum.TriggerTypes.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.TriggerId.Companion.TriggerId
import io.hamal.lib.domain.vo.TriggerStatus.Companion.TriggerStatus
import io.hamal.lib.domain.vo.TriggerType.Companion.TriggerType
import java.lang.reflect.Type

interface TriggerRepository : TriggerCmdRepository, TriggerQueryRepository

interface TriggerCmdRepository : CmdRepository {
    fun create(cmd: CreateFixedRateCmd): Trigger.FixedRate
    fun create(cmd: CreateEventCmd): Trigger.Event
    fun create(cmd: CreateHookCmd): Trigger.Hook
    fun create(cmd: CreateCronCmd): Trigger.Cron
    fun create(cmd: CreateEndpointCmd): Trigger.Endpoint
    fun set(triggerId: TriggerId, cmd: SetTriggerStatusCmd): Trigger
    fun delete(cmd: DeleteCmd)

    data class CreateFixedRateCmd(
        val id: CmdId,
        val triggerId: TriggerId,
        val workspaceId: WorkspaceId,
        val name: TriggerName,
        val funcId: FuncId,
        val namespaceId: NamespaceId,
        val inputs: TriggerInputs,
        val duration: TriggerDuration,
        val correlationId: CorrelationId? = null,
        val status: TriggerStatus = TriggerStatus(Active)
    )

    data class CreateEventCmd(
        val id: CmdId,
        val triggerId: TriggerId,
        val workspaceId: WorkspaceId,
        val name: TriggerName,
        val funcId: FuncId,
        val namespaceId: NamespaceId,
        val inputs: TriggerInputs,
        val topicId: TopicId,
        val correlationId: CorrelationId? = null,
        val status: TriggerStatus = TriggerStatus(Active)
    )

    data class CreateHookCmd(
        val id: CmdId,
        val triggerId: TriggerId,
        val workspaceId: WorkspaceId,
        val name: TriggerName,
        val funcId: FuncId,
        val namespaceId: NamespaceId,
        val inputs: TriggerInputs,
        val correlationId: CorrelationId? = null,
        val status: TriggerStatus = TriggerStatus(Active)
    )

    data class CreateCronCmd(
        val id: CmdId,
        val triggerId: TriggerId,
        val workspaceId: WorkspaceId,
        val name: TriggerName,
        val funcId: FuncId,
        val namespaceId: NamespaceId,
        val inputs: TriggerInputs,
        val cron: CronPattern,
        val correlationId: CorrelationId? = null,
        val status: TriggerStatus = TriggerStatus(Active)
    )

    data class CreateEndpointCmd(
        val id: CmdId,
        val triggerId: TriggerId,
        val workspaceId: WorkspaceId,
        val name: TriggerName,
        val funcId: FuncId,
        val namespaceId: NamespaceId,
        val inputs: TriggerInputs,
        val correlationId: CorrelationId? = null,
        val status: TriggerStatus = TriggerStatus(Active)
    )

    data class SetTriggerStatusCmd(
        val id: CmdId,
        val status: TriggerStatus
    )

    data class DeleteCmd(
        val id: CmdId,
        val triggerId: TriggerId
    )
}

interface TriggerQueryRepository {
    fun get(triggerId: TriggerId) = find(triggerId) ?: throw NoSuchElementException("Trigger not found")
    fun find(triggerId: TriggerId): Trigger?
    fun list(query: TriggerQuery): List<Trigger>
    fun count(query: TriggerQuery): Count

    data class TriggerQuery(
        var afterId: TriggerId = TriggerId(SnowflakeId(Long.MAX_VALUE)),
        var types: List<TriggerType> = entries.map(::TriggerType).toList(),
        var limit: Limit = Limit(1),
        var triggerIds: List<TriggerId> = listOf(),
        var funcIds: List<FuncId> = listOf(),
        var topicIds: List<TopicId> = listOf(),
        var workspaceIds: List<WorkspaceId> = listOf(),
        var namespaceIds: List<NamespaceId> = listOf()
    )
}

sealed interface Trigger : DomainObject<TriggerId>, HasNamespaceId, HasWorkspaceId {
    val cmdId: CmdId
    override val workspaceId: WorkspaceId
    val name: TriggerName
    val funcId: FuncId
    override val namespaceId: NamespaceId
    val correlationId: CorrelationId?
    val inputs: TriggerInputs
    val type: TriggerType
    val status: TriggerStatus

    object Adapter : AdapterGeneric<Trigger> {
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
            val type = context.deserialize<TriggerType>(
                json.asJsonObject.get("type"), TriggerType::class.java
            )
            return context.deserialize(json, classMapping[type.enumValue]!!.java)
        }

        private val classMapping = mapOf(
            FixedRate to Trigger.FixedRate::class,
            Event to Trigger.Event::class,
            Hook to Trigger.Hook::class,
            Cron to Trigger.Cron::class,
            Endpoint to Trigger.Endpoint::class
        )
    }

    class FixedRate(
        override val cmdId: CmdId,
        override val id: TriggerId,
        override val updatedAt: UpdatedAt,
        override val workspaceId: WorkspaceId,
        override val name: TriggerName,
        override val funcId: FuncId,
        override val namespaceId: NamespaceId,
        override val inputs: TriggerInputs,
        override val status: TriggerStatus,
        val duration: TriggerDuration,
        override val correlationId: CorrelationId? = null
    ) : Trigger {
        override val type = TriggerType(FixedRate)
    }

    class Event(
        override val cmdId: CmdId,
        override val id: TriggerId,
        override val updatedAt: UpdatedAt,
        override val workspaceId: WorkspaceId,
        override val name: TriggerName,
        override val funcId: FuncId,
        override val namespaceId: NamespaceId,
        override val inputs: TriggerInputs,
        override val status: TriggerStatus,
        val topicId: TopicId,
        override val correlationId: CorrelationId? = null
    ) : Trigger {
        override val type = TriggerType(Event)
    }

    class Endpoint(
        override val cmdId: CmdId,
        override val id: TriggerId,
        override val updatedAt: UpdatedAt,
        override val workspaceId: WorkspaceId,
        override val name: TriggerName,
        override val funcId: FuncId,
        override val namespaceId: NamespaceId,
        override val inputs: TriggerInputs,
        override val status: TriggerStatus,
        override val correlationId: CorrelationId? = null
    ) : Trigger {
        override val type = TriggerType(Endpoint)
    }

    class Hook(
        override val cmdId: CmdId,
        override val id: TriggerId,
        override val updatedAt: UpdatedAt,
        override val workspaceId: WorkspaceId,
        override val name: TriggerName,
        override val funcId: FuncId,
        override val namespaceId: NamespaceId,
        override val inputs: TriggerInputs,
        override val status: TriggerStatus,
        override val correlationId: CorrelationId? = null
    ) : Trigger {
        override val type = TriggerType(Hook)
    }

    class Cron(
        override val cmdId: CmdId,
        override val id: TriggerId,
        override val updatedAt: UpdatedAt,
        override val workspaceId: WorkspaceId,
        override val name: TriggerName,
        override val funcId: FuncId,
        override val namespaceId: NamespaceId,
        override val inputs: TriggerInputs,
        override val status: TriggerStatus,
        override val correlationId: CorrelationId? = null,
        val cron: CronPattern
    ) : Trigger {
        override val type = TriggerType(Cron)
    }

}

