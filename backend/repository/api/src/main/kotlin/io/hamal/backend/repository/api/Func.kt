package io.hamal.backend.repository.api

import io.hamal.backend.core.func.Func
import io.hamal.backend.core.trigger.Trigger
import io.hamal.backend.repository.api.FuncRepository.Command.FuncToCreate
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncRef
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.base.referenceFromId
import io.hamal.lib.domain.vo.port.DomainIdGeneratorAdapter
import io.hamal.lib.domain.vo.port.GenerateDomainIdPort


interface FuncRepository {

    fun get(id: FuncId): Func

    fun getTrigger(id: TriggerId): Trigger

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
            var ref: FuncRef,
            var code: Code
        ) : Command {
            override val order = Order.InsertPrimary
        }
    }

    class Recorder(
        val generateDomainId: GenerateDomainIdPort
    ) {

        internal val commands = mutableListOf<Command>()
        fun commands(): List<Command> {
            return commands.toList()
        }
    }
}

fun FuncRepository.Recorder.createFunc(block: FuncToCreate.() -> Unit): FuncId {
    val result = generateDomainId(Shard(0), ::FuncId)
    commands.add(
        FuncToCreate(
            funcId = result,
            ref = referenceFromId(result, ::FuncRef),
            code = Code("")
        ).apply(block)
    )
    return result
}