package io.hamal.backend.repository.memory

import io.hamal.backend.repository.api.FuncCmdRepository
import io.hamal.backend.repository.api.FuncCmdRepository.Command
import io.hamal.backend.repository.api.FuncCmdRepository.Command.FuncToCreate
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.repository.api.domain.Func
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncName

object MemoryFuncRepository : FuncCmdRepository, FuncQueryRepository {

    internal val funcs = mutableMapOf<FuncId, FuncEntity>()
    internal val reqIds = mutableSetOf<ReqId>()

    override fun get(id: FuncId): Func {
        return funcs[id]?.let(FuncEntity::toModel)
            ?: throw IllegalArgumentException("No func found with $id")
    }

//    override fun getTrigger(id: TriggerId): Trigger {
//        return requireNotNull(triggers[id]?.let(TriggerEntity::toModel)) { "No trigger found with $id" }
//    }

    override fun execute(reqId: ReqId, commands: List<Command>): List<Func> {
        check(reqIds.add(reqId)) { "Request $reqId was already executed" }
        val groupedCommands = commands.groupBy { it.funcId }
        groupedCommands.forEach { id, cmds ->
            cmds.sortedBy { it.order }.forEach { cmd ->
                when (cmd) {
                    is FuncToCreate -> createFunc(cmd)
                    else -> TODO("$cmd not supported")
                }
            }
        }

        return groupedCommands.keys.map(this::get)
    }

    override fun find(funcId: FuncId): Func? {
        return funcs[funcId]?.toModel()
    }

    override fun list(afterId: FuncId, limit: Int): List<Func> {
        return funcs.keys.sorted()
            .dropWhile { it <= afterId }
            .take(limit)
            .mapNotNull { find(it) }
            .reversed()
    }
}

internal fun MemoryFuncRepository.createFunc(toCreate: FuncToCreate) {
    funcs[toCreate.funcId] = FuncEntity(
        id = toCreate.funcId,
        reference = toCreate.name,
        code = toCreate.code
    )
}

internal data class FuncEntity(
    val id: FuncId,
    val reference: FuncName,
    val code: Code
) {
    fun toModel(): Func {
        return Func(
            id = this.id,
            name = this.reference,
            code = this.code
        )
    }
}
