package io.hamal.backend.store.api

import io.hamal.backend.core.model.JobDefinition
import io.hamal.backend.store.api.JobDefinitionStore.Command.JobDefinitionToInsert
import io.hamal.backend.store.api.JobDefinitionStore.Command.ManualTriggerToInsert
import io.hamal.lib.RequestId
import io.hamal.lib.Shard
import io.hamal.lib.vo.JobDefinitionId
import io.hamal.lib.vo.JobReference
import io.hamal.lib.vo.TriggerId
import io.hamal.lib.vo.TriggerReference
import io.hamal.lib.vo.base.referenceFromId
import io.hamal.lib.vo.port.FixedTimeIdGeneratorAdapter
import io.hamal.lib.vo.port.GenerateDomainIdPort


abstract class JobDefinitionStore {

    abstract fun get(id: JobDefinitionId): JobDefinition

    abstract fun execute(requestId: RequestId, commands: List<JobDefinitionStore.Command>): List<JobDefinition>

    // jobDefinitionRequest
    fun request(requestId: RequestId, record: (Recorder) -> Unit): List<JobDefinition> {
        val recorder = Recorder(FixedTimeIdGeneratorAdapter()) //FIXME
        record(recorder)
        return execute(requestId, recorder.commands)
    }

    interface Command {

        enum class Order {
            InsertPrimary,
            InsertSecondary,
            Update,
            Delete
        }

        val jobDefinitionId: JobDefinitionId
        val order: Order
//        val requestId: RequestId

        data class JobDefinitionToInsert(
//            override val requestId: RequestId,
            override val jobDefinitionId: JobDefinitionId,
            var reference: JobReference
        ) : Command {
            override val order = Order.InsertPrimary
        }


        data class ManualTriggerToInsert(
            override val jobDefinitionId: JobDefinitionId,
            val reference: TriggerReference,
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

fun JobDefinitionStore.Recorder.insertJobDefinition(block: JobDefinitionToInsert.() -> Unit): JobDefinitionId {
    val result = generateDomainId(Shard(0), ::JobDefinitionId)
    commands.add(
        JobDefinitionToInsert(
            jobDefinitionId = result,
            reference = referenceFromId(result, ::JobReference)
        ).apply(block)
    )
    return result
}

fun JobDefinitionStore.Recorder.insertManualTrigger(
    jobDefinitionId: JobDefinitionId,
    block: ManualTriggerToInsert.() -> Unit
): TriggerId {
    val result = generateDomainId(Shard(0), ::TriggerId)
    commands.add(
        ManualTriggerToInsert(
            jobDefinitionId = jobDefinitionId,
            reference = referenceFromId(result, ::TriggerReference)
        ).apply(block)
    )
    return result
}
