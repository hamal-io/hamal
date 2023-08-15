package io.hamal.backend.repository.api

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable
import kotlin.time.Duration


interface TriggerCmdRepository {
    fun create(cmd: CreateFixedRateCmd): FixedRateTrigger
    fun create(cmd: CreateEventCmd): EventTrigger
    fun clear()
    data class CreateFixedRateCmd(
        val id: CmdId,
        val triggerId: TriggerId,
        val name: TriggerName,
        val funcId: FuncId,
        val namespaceId: NamespaceId,
        val inputs: TriggerInputs,
        val duration: Duration,
        val correlationId: CorrelationId? = null
    )

    data class CreateEventCmd(
        val id: CmdId,
        val triggerId: TriggerId,
        val name: TriggerName,
        val funcId: FuncId,
        val namespaceId: NamespaceId,
        val inputs: TriggerInputs,
        val topicId: TopicId,
        val correlationId: CorrelationId? = null
    )
}

interface TriggerQueryRepository {
    fun get(triggerId: TriggerId) = find(triggerId) ?: throw NoSuchElementException("Trigger not found")
    fun find(triggerId: TriggerId): Trigger?

    fun list(block: TriggerQuery.() -> Unit): List<Trigger>

    data class TriggerQuery(
        var afterId: TriggerId = TriggerId(SnowflakeId(Long.MAX_VALUE)),
        var types: Set<TriggerType> = TriggerType.values().toSet(),
        var limit: Limit = Limit(1)
    )

}


@Serializable
sealed interface Trigger : DomainObject<TriggerId> {
    val cmdId: CmdId
    val name: TriggerName
    val funcId: FuncId
    val namespaceId: NamespaceId
    val correlationId: CorrelationId?
    val inputs: TriggerInputs
}

@Serializable
class FixedRateTrigger(
    override val cmdId: CmdId,
    override val id: TriggerId,
    override val name: TriggerName,
    override val funcId: FuncId,
    override val namespaceId: NamespaceId,
    override val inputs: TriggerInputs,
    val duration: Duration,
    override val correlationId: CorrelationId? = null
) : Trigger

@Serializable
class EventTrigger(
    override val cmdId: CmdId,
    override val id: TriggerId,
    override val name: TriggerName,
    override val funcId: FuncId,
    override val namespaceId: NamespaceId,
    override val inputs: TriggerInputs,
    val topicId: TopicId,
    override val correlationId: CorrelationId? = null
) : Trigger
