package io.hamal.backend.repository.memory.domain

import io.hamal.backend.core.func.Func
import io.hamal.backend.core.trigger.Trigger
import io.hamal.backend.repository.api.FuncRepository
import io.hamal.backend.repository.api.FuncRepository.Command
import io.hamal.backend.repository.api.FuncRepository.Command.FuncToCreate
import io.hamal.backend.repository.api.FuncRepository.Command.ManualTriggerToCreate
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.*

object MemoryFuncRepository : FuncRepository {

    internal val funcs = mutableMapOf<FuncId, FuncEntity>()
    internal val triggers = mutableMapOf<TriggerId, TriggerEntity>()

    internal val reqIds = mutableSetOf<ReqId>()

    override fun get(id: FuncId): Func {
        return funcs[id]?.let(FuncEntity::toModel)
            ?: throw IllegalArgumentException("No func found with $id")
    }

    override fun getTrigger(id: TriggerId): Trigger {
        return requireNotNull(triggers[id]?.let(TriggerEntity::toModel)) { "No trigger found with $id" }
    }

    override fun execute(reqId: ReqId, commands: List<Command>): List<Func> {
        check(reqIds.add(reqId)) { "Request $reqId was already executed" }
        val groupedCommands = commands.groupBy { it.funcId }
        groupedCommands.forEach { id, cmds ->
            cmds.sortedBy { it.order }.forEach { cmd ->
                when (cmd) {
                    is FuncToCreate -> createFunc(cmd)
                    is ManualTriggerToCreate -> createManualTrigger(cmd)
                    else -> TODO("$cmd not supported")
                }
            }
        }

        return groupedCommands.keys.map(this::get)
    }
}

internal fun MemoryFuncRepository.createFunc(toCreate: FuncToCreate) {
    funcs[toCreate.funcId] = FuncEntity(
        id = toCreate.funcId,
        reference = toCreate.ref,
        triggers = mutableListOf(),
        code = toCreate.code
    )
}

internal fun MemoryFuncRepository.createManualTrigger(toCreate: ManualTriggerToCreate) {
    triggers[toCreate.id] = TriggerEntity(
        id = toCreate.id,
        reference = toCreate.reference,
        funcId = toCreate.funcId
    )
    funcs[toCreate.funcId]!!.triggers.add(toCreate.id)
}

internal data class FuncEntity(
    val id: FuncId,
    val reference: FuncRef,
    val triggers: MutableList<TriggerId>,
    val code: Code
) {
    fun toModel(): Func {
        return Func(
            id = this.id,
            reference = this.reference,
            triggers = this.triggers.map(MemoryFuncRepository::getTrigger),
            code = this.code
        )
    }
}


internal data class TriggerEntity(
    val id: TriggerId,
    val funcId: FuncId,
    var reference: TriggerRef
) {
    fun toModel(): Trigger {
        return Trigger.ManualTrigger(
            id = this.id,
            reference = this.reference,
            funcId = this.funcId
        )
    }
}