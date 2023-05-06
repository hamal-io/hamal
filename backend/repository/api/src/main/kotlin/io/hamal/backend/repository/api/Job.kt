package io.hamal.backend.repository.api

import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.backend.repository.api.JobDefinitionRepository.Command.JobDefinitionToCreate
import io.hamal.backend.repository.api.JobDefinitionRepository.Command.ManualTriggerToInsert
import io.hamal.lib.RequestId
import io.hamal.lib.Shard
import io.hamal.lib.vo.JobDefinitionId
import io.hamal.lib.vo.JobReference
import io.hamal.lib.vo.TriggerId
import io.hamal.lib.vo.TriggerReference
import io.hamal.lib.vo.base.referenceFromId
import io.hamal.lib.vo.port.DomainIdGeneratorAdapter
import io.hamal.lib.vo.port.GenerateDomainIdPort


interface JobDefinitionRepository {

    fun get(id: JobDefinitionId): JobDefinition

    fun execute(requestId: RequestId, commands: List<Command>): List<JobDefinition>

    // jobDefinitionRequest
    fun request(requestId: RequestId, record: Recorder.() -> Unit): List<JobDefinition> {
        val recorder = Recorder(DomainIdGeneratorAdapter) //FIXME
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

        val order: Order
        val jobDefinitionId: JobDefinitionId

        data class JobDefinitionToCreate(
            override val jobDefinitionId: JobDefinitionId,
            var reference: JobReference
        ) : Command {
            override val order = Order.InsertPrimary
        }


        data class ManualTriggerToInsert(
            override val jobDefinitionId: JobDefinitionId,
            var reference: TriggerReference,
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

fun JobDefinitionRepository.Recorder.createJobDefinition(block: JobDefinitionToCreate.() -> Unit): JobDefinitionId {
    val result = generateDomainId(Shard(0), ::JobDefinitionId)
    commands.add(
        JobDefinitionToCreate(
            jobDefinitionId = result,
            reference = referenceFromId(result, ::JobReference)
        ).apply(block)
    )
    return result
}

fun JobDefinitionRepository.Recorder.createManualTrigger(
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
