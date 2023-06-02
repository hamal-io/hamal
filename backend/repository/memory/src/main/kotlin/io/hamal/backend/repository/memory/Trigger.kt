package io.hamal.backend.repository.memory

import io.hamal.backend.repository.api.TriggerCmdRepository
import io.hamal.backend.repository.api.TriggerCmdRepository.Command
import io.hamal.backend.repository.api.TriggerCmdRepository.Command.FixedRateTriggerToCreate
import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.backend.repository.api.TriggerQueryRepository.Query
import io.hamal.backend.repository.api.domain.FixedRateTrigger
import io.hamal.backend.repository.api.domain.Trigger
import io.hamal.lib.domain.ComputeId
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain._enum.TriggerType.FixedRate
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.TriggerName
import kotlin.time.Duration

object MemoryTriggerRepository : TriggerCmdRepository, TriggerQueryRepository {

    internal val triggers = mutableMapOf<TriggerId, TriggerEntity>()

    internal val computeIds = mutableSetOf<ComputeId>()

    override fun get(id: TriggerId): Trigger {
        return requireNotNull(triggers[id]?.let(TriggerEntity::toModel)) { "No trigger found with $id" }

    }

    override fun execute(computeId: ComputeId, commands: List<Command>): List<Trigger> {
        check(computeIds.add(computeId)) { "$computeId already executed" }
        val groupedCommands = commands.groupBy { it.id }
        groupedCommands.forEach { id, cmds ->
            cmds.sortedBy { it.order }.forEach { cmd ->
                when (cmd) {
                    is FixedRateTriggerToCreate -> createFixedRateTrigger(cmd)
                    else -> TODO("$cmd not supported")
                }
            }
        }

        return groupedCommands.keys.map(this::get)
    }

    override fun find(triggerId: TriggerId): Trigger? {
        return triggers[triggerId]?.toModel()
    }

    override fun list(afterId: TriggerId, limit: Int): List<Trigger> {
        return triggers.keys.sorted()
            .dropWhile { it <= afterId }
            .take(limit)
            .mapNotNull { find(it) }
            .reversed()
    }

    override fun query(block: Query.() -> Unit): List<Trigger> {
        val query = Query(
            afterId = TriggerId(0),
            limit = Int.MAX_VALUE
        )
        block(query)

        return triggers.keys.sorted()
            .dropWhile { it <= query.afterId }
            .take(query.limit)
            .mapNotNull { find(it) }
            .reversed()
    }
}


internal fun MemoryTriggerRepository.createFixedRateTrigger(toCreate: FixedRateTriggerToCreate) {
    triggers[toCreate.id] = TriggerEntity(
        id = toCreate.id,
        name = toCreate.name,
        type = FixedRate,
        funcId = toCreate.funcId,
        duration = toCreate.duration
    )
}


internal data class TriggerEntity(
    val id: TriggerId,
    var name: TriggerName,
    val type: TriggerType,
    var funcId: FuncId,
    var duration: Duration?
) {
    fun toModel(): Trigger {
        return FixedRateTrigger(
            id = id,
            name = name,
            funcId = funcId,
            duration = duration!!
        )
    }
}