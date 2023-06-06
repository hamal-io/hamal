package io.hamal.backend.repository.api

import io.hamal.backend.repository.api.domain.Trigger
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.*
import kotlin.time.Duration


interface TriggerCmdRepository {
    fun create(cmd: CreateFixedRateCmd): Trigger
    data class CreateFixedRateCmd(
        val id: CmdId,
        val accountId: AccountId,
        val triggerId: TriggerId,
        val name: TriggerName,
        val funcId: FuncId,
        val inputs: TriggerInputs,
        val secrets: TriggerSecrets,
        val duration: Duration
    )
}

interface TriggerQueryRepository {
    fun find(triggerId: TriggerId): Trigger?
    fun list(afterId: TriggerId, limit: Int): List<Trigger>

    fun query(block: Query.() -> Unit): List<Trigger>

    data class Query(
        var afterId: TriggerId,
        var limit: Int
    )

}

