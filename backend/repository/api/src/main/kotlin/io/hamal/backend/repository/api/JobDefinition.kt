package io.hamal.backend.repository.api

import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.backend.core.task.Task
import io.hamal.backend.core.trigger.Trigger
import io.hamal.backend.repository.api.JobDefinitionRepository.Command.*
import io.hamal.lib.domain.RequestId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.base.referenceFromId
import io.hamal.lib.domain.vo.port.DomainIdGeneratorAdapter
import io.hamal.lib.domain.vo.port.GenerateDomainIdPort


interface JobDefinitionRepository {

    fun get(id: JobDefinitionId): JobDefinition

    fun getTrigger(id: TriggerId): Trigger

    fun getTask(id: TaskId): Task

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

        data class ScriptTaskToCreate(
            val id: TaskId,
            override val jobDefinitionId: JobDefinitionId
        ) : Command {
            override val order = Order.InsertSecondary
        }

        data class ManualTriggerToCreate(
            val id: TriggerId,
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
    block: ManualTriggerToCreate.() -> Unit
): TriggerId {
    val result = generateDomainId(Shard(0), ::TriggerId)
    commands.add(
        ManualTriggerToCreate(
            id = result,
            jobDefinitionId = jobDefinitionId,
            reference = referenceFromId(result, ::TriggerReference)
        ).apply(block)
    )
    return result
}

fun JobDefinitionRepository.Recorder.createScriptTask(
    jobDefinitionId: JobDefinitionId,
    block: ScriptTaskToCreate.() -> Unit
): TaskId {
    val result = generateDomainId(Shard(0), ::TaskId)
    commands.add(
        ScriptTaskToCreate(
            id = result,
            jobDefinitionId = jobDefinitionId
        ).apply(block)
    )
    return result
}