package io.hamal.backend.repository.memory

import io.hamal.backend.core.trigger.Trigger
import io.hamal.backend.repository.api.TriggerRepository
import io.hamal.backend.repository.api.TriggerRepository.Command
import io.hamal.backend.repository.api.TriggerRepository.Command.ManualTriggerToCreate
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.*

object MemoryTriggerRepository : TriggerRepository {

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
                    is ManualTriggerToCreate -> createManualTrigger(cmd)
                    else -> TODO("$cmd not supported")
                }
            }
        }

        return groupedCommands.keys.map(this::get)
    }
}


internal fun MemoryTriggerRepository.createManualTrigger(toCreate: ManualTriggerToCreate) {
    triggers[toCreate.id] = TriggerEntity(
        id = toCreate.id,
        reference = toCreate.reference,
    )
}


internal data class TriggerEntity(
    val id: TriggerId,
    var reference: TriggerName
) {
    fun toModel(): Trigger {
        return Trigger.ManualTrigger(
            id = this.id,
            reference = this.reference,
        )
    }
}