package io.hamal.backend.repository.api

import io.hamal.backend.core.trigger.Trigger
import io.hamal.backend.repository.api.TriggerRepository.Command.ScheduleTriggerToCreate
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.TriggerName
import io.hamal.lib.domain.vo.port.DomainIdGeneratorAdapter
import io.hamal.lib.domain.vo.port.GenerateDomainIdPort


interface TriggerRepository {
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
            //inputs
            //secrets
        ) : Command {
            override val order = Order.InsertSecondary
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

fun TriggerRepository.Recorder.createScheduleTrigger(
    block: ScheduleTriggerToCreate.() -> Unit
): TriggerId {
    val result = generateDomainId(Shard(0), ::TriggerId)
    commands.add(
        ScheduleTriggerToCreate(
            id = result,
            name = TriggerName("TBD")
        ).apply(block)
    )
    return result
}