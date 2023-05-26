package io.hamal.backend.repository.api

import io.hamal.backend.repository.api.TriggerCmdRepository.Command.FixedRateTriggerToCreate
import io.hamal.backend.repository.api.domain.trigger.Trigger
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.TriggerName
import io.hamal.lib.domain.vo.port.DomainIdGeneratorAdapter
import io.hamal.lib.domain.vo.port.GenerateDomainId
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days


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

        data class FixedRateTriggerToCreate(
            override val id: TriggerId,
            var name: TriggerName,
            var funcId: FuncId,
            var duration: Duration
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

    fun query(block: Query.() -> Unit): List<Trigger>

    data class Query(
        var afterId: TriggerId,
        var limit: Int
    )

}


fun TriggerCmdRepository.Recorder.createScheduleTrigger(
    block: FixedRateTriggerToCreate.() -> Unit
): TriggerId {
    val result = generateDomainId(Shard(0), ::TriggerId)
    commands.add(
        FixedRateTriggerToCreate(
            id = result,
            name = TriggerName("TBD"),
            funcId = FuncId(0),
            duration = 1.days
        ).apply(block)
    )
    return result
}