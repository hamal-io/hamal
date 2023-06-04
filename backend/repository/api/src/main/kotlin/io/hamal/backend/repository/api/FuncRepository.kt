package io.hamal.backend.repository.api

import io.hamal.backend.repository.api.FuncCmdRepository.Command.FuncToCreate
import io.hamal.backend.repository.api.domain.Func
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.port.DefaultDomainIdGenerator
import io.hamal.lib.domain.vo.port.GenerateDomainId


interface FuncCmdRepository {

    fun get(id: FuncId): Func

    fun execute(cmdId: CmdId, commands: List<Command>): List<Func>

    fun request(cmdId: CmdId, record: Recorder.() -> Unit): List<Func> {
        val recorder = Recorder(DefaultDomainIdGenerator) //FIXME
        record(recorder)
        return execute(cmdId, recorder.commands)
    }

    interface Command {

        enum class Order {
            InsertPrimary,
            InsertSecondary,
            Update,
            Delete
        }

        val order: Order
        val funcId: FuncId

        data class FuncToCreate(
            override val funcId: FuncId,
            var name: FuncName,
            var code: Code
        ) : Command {
            override val order = Order.InsertPrimary
        }
    }

    class Recorder(
        val generateDomainId: GenerateDomainId
    ) {

        internal val commands = mutableListOf<Command>()
        fun commands(): List<Command> {
            return commands.toList()
        }
    }
}

interface FuncQueryRepository {
    fun find(funcId: FuncId): Func?

    fun list(afterId: FuncId, limit: Int): List<Func>
}

fun FuncCmdRepository.Recorder.createFunc(block: FuncToCreate.() -> Unit): FuncId {
    val result = generateDomainId(Shard(0), ::FuncId)
    commands.add(
        FuncToCreate(
            funcId = result,
            name = FuncName("TBD"),
            code = Code("")
        ).apply(block)
    )
    return result
}