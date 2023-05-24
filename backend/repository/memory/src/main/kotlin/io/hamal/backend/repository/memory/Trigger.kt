package io.hamal.backend.repository.memory

import io.hamal.backend.core.trigger.ScheduleTrigger
import io.hamal.backend.core.trigger.Trigger
import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.backend.repository.api.TriggerRequestRepository
import io.hamal.backend.repository.api.TriggerRequestRepository.Command
import io.hamal.backend.repository.api.TriggerRequestRepository.Command.ScheduleTriggerToCreate
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.TriggerName

object MemoryTriggerRepository : TriggerRequestRepository, TriggerQueryRepository {

    internal val triggers = mutableMapOf<TriggerId, TriggerEntity>()

    internal val reqIds = mutableSetOf<ReqId>()

    override fun get(id: TriggerId): Trigger {
        return requireNotNull(triggers[id]?.let(TriggerEntity::toModel)) { "No trigger found with $id" }

    }

    override fun execute(reqId: ReqId, commands: List<Command>): List<Trigger> {
        check(reqIds.add(reqId)) { "Request $reqId was already executed" }
        val groupedCommands = commands.groupBy { it.id }
        groupedCommands.forEach { id, cmds ->
            cmds.sortedBy { it.order }.forEach { cmd ->
                when (cmd) {
                    is ScheduleTriggerToCreate -> createScheduleTrigger(cmd)
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
}


internal fun MemoryTriggerRepository.createScheduleTrigger(toCreate: ScheduleTriggerToCreate) {
    triggers[toCreate.id] = TriggerEntity(
        id = toCreate.id,
        name = toCreate.name,
        code = toCreate.code
    )
}


internal data class TriggerEntity(
    val id: TriggerId,
    var name: TriggerName,
    var code: Code
) {
    fun toModel(): Trigger {
        return ScheduleTrigger(
            id = id,
            name = name,
            code = code
        )
    }
}