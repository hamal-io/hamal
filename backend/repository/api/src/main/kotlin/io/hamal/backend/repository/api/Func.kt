package io.hamal.backend.repository.api

import io.hamal.backend.repository.api.domain.func.Func
import io.hamal.backend.repository.api.FuncCmdRepository.Command.FuncToCreate
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.port.DomainIdGeneratorAdapter
import io.hamal.lib.domain.vo.port.GenerateDomainId


interface FuncCmdRepository {

    fun get(id: FuncId): Func

    fun execute(reqId: ReqId, commands: List<Command>): List<Func>

    fun request(reqId: ReqId, record: Recorder.() -> Unit): List<Func> {
        val recorder = Recorder(DomainIdGeneratorAdapter) //FIXME
        record(recorder)
        return execute(reqId, recorder.commands)
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