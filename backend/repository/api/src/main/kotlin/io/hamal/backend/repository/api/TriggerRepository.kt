package io.hamal.backend.repository.api

import io.hamal.backend.repository.api.domain.EventTrigger
import io.hamal.backend.repository.api.domain.FixedRateTrigger
import io.hamal.backend.repository.api.domain.Trigger
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import kotlin.time.Duration


interface TriggerCmdRepository {
    fun create(cmd: CreateFixedRateCmd): FixedRateTrigger

    fun create(cmd: CreateEventCmd): EventTrigger

    data class CreateFixedRateCmd(
        val id: CmdId,
        val tenantId: TenantId,
        val triggerId: TriggerId,
        val name: TriggerName,
        val funcId: FuncId,
        val inputs: TriggerInputs,
        val secrets: TriggerSecrets,
        val duration: Duration
    )

    data class CreateEventCmd(
        val id: CmdId,
        val tenantId: TenantId,
        val triggerId: TriggerId,
        val name: TriggerName,
        val funcId: FuncId,
        val inputs: TriggerInputs,
        val secrets: TriggerSecrets,
        val topicId: TopicId
    )
}

interface TriggerQueryRepository {
    fun find(triggerId: TriggerId): Trigger?

    fun query(block: Query.() -> Unit): List<Trigger>

    data class Query(
        var afterId: TriggerId,
        var types: Set<TriggerType>,
        var limit: Int
    )

}

