package io.hamal.backend.repository.api

import io.hamal.backend.repository.api.domain.trigger.Trigger
import io.hamal.backend.repository.api.TriggerCmdRepository.Command.ScheduleTriggerToCreate
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.TriggerName
import io.hamal.lib.domain.vo.port.DomainIdGeneratorAdapter
import io.hamal.lib.domain.vo.port.GenerateDomainId


interface TriggerCmdRepository {
    fun get(id: TriggerId): Trigger

    fun execute(reqId: ReqId, commands: List<Command>): List<Trigger>

    fun request(reqId: ReqId, record: Recorder.() -> Unit): List<Trigger> {
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
        val id: TriggerId

        data class ScheduleTriggerToCreate(
            override val id: TriggerId,
            var name: TriggerName,
            var code: Code
            //inputs
            //secrets
        ) : Command {
            override val order = Order.InsertSecondary
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

interface TriggerQueryRepository {
    fun find(TriggerId: TriggerId): Trigger?

    fun list(afterId: TriggerId, limit: Int): List<Trigger>
}


fun TriggerCmdRepository.Recorder.createScheduleTrigger(
    block: ScheduleTriggerToCreate.() -> Unit
): TriggerId {
    val result = generateDomainId(Shard(0), ::TriggerId)
    commands.add(
        ScheduleTriggerToCreate(
            id = result,
            name = TriggerName("TBD"),
            code = Code("")
        ).apply(block)
    )
    return result
}