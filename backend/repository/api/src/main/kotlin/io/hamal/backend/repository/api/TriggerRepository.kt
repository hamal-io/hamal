package io.hamal.backend.repository.api

import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.EventTrigger
import io.hamal.lib.domain.FixedRateTrigger
import io.hamal.lib.domain.Trigger
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
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
        val inputs: TriggerInputs,
        val secrets: TriggerSecrets,
        val duration: Duration
    )

    data class CreateEventCmd(
        val id: CmdId,
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

    fun list(block: TriggerQuery.() -> Unit): List<Trigger>

    data class TriggerQuery(
        var afterId: TriggerId = TriggerId(0),
        var types: Set<TriggerType> = TriggerType.values().toSet(),
        var limit: Limit = Limit(1)
    )

}

