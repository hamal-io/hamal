package io.hamal.backend.repository.memory.domain

import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.backend.core.task.ScriptTask
import io.hamal.backend.core.task.Task
import io.hamal.backend.core.task.Task.Type.Script
import io.hamal.backend.core.trigger.Trigger
import io.hamal.backend.repository.api.JobDefinitionRepository
import io.hamal.backend.repository.api.JobDefinitionRepository.Command
import io.hamal.backend.repository.api.JobDefinitionRepository.Command.*
import io.hamal.lib.domain.RequestId
import io.hamal.lib.domain.vo.*

object MemoryJobDefinitionRepository : JobDefinitionRepository {

    internal val jobDefinitions = mutableMapOf<JobDefinitionId, JobDefinitionEntity>()
    internal val tasks = mutableMapOf<TaskId, TaskEntity>()
    internal val triggers = mutableMapOf<TriggerId, TriggerEntity>()

    internal val requestIds = mutableSetOf<RequestId>()

    override fun get(id: JobDefinitionId): JobDefinition {
        return jobDefinitions[id]?.let(JobDefinitionEntity::toModel)
            ?: throw IllegalArgumentException("No job definition found with $id")
    }

    override fun getTrigger(id: TriggerId): Trigger {
        return requireNotNull(triggers[id]?.let(TriggerEntity::toModel)) { "No trigger found with $id" }
    }

    override fun getTask(id: TaskId): Task {
        return requireNotNull(tasks[id]?.let(TaskEntity::toModel)) { "No task found with $id" }
    }

    override fun execute(requestId: RequestId, commands: List<Command>): List<JobDefinition> {
        check(requestIds.add(requestId)) { "Request $requestId was already executed" }
        val groupedCommands = commands.groupBy { it.jobDefinitionId }
        groupedCommands.forEach { id, cmds ->
            cmds.sortedBy { it.order }.forEach { cmd ->
                when (cmd) {
                    is JobDefinitionToCreate -> createJobDefinition(cmd)
                    is ManualTriggerToCreate -> createManualTrigger(cmd)
                    is ScriptTaskToCreate -> createScriptTask(cmd)
                    else -> TODO("$cmd not supported")
                }
            }
        }

        return groupedCommands.keys.map(this::get)
    }
}

internal fun MemoryJobDefinitionRepository.createJobDefinition(toCreate: JobDefinitionToCreate) {
    jobDefinitions[toCreate.jobDefinitionId] = JobDefinitionEntity(
        id = toCreate.jobDefinitionId,
        reference = toCreate.reference,
        tasks = mutableListOf(),
        triggers = mutableListOf()
    )
}

internal fun MemoryJobDefinitionRepository.createManualTrigger(toCreate: ManualTriggerToCreate) {
    triggers[toCreate.id] = TriggerEntity(
        id = toCreate.id,
        reference = toCreate.reference,
        jobDefinitionId = toCreate.jobDefinitionId
    )
    jobDefinitions[toCreate.jobDefinitionId]!!.triggers.add(toCreate.id)
}

internal fun MemoryJobDefinitionRepository.createScriptTask(toCreate: ScriptTaskToCreate) {
    tasks[toCreate.id] = TaskEntity(
        id = toCreate.id,
        type = Script,
        jobDefinitionId = toCreate.jobDefinitionId,
    )
    jobDefinitions[toCreate.jobDefinitionId]!!.tasks.add(toCreate.id)
}

internal data class JobDefinitionEntity(
    val id: JobDefinitionId,
    val reference: JobReference,
    val tasks: MutableList<TaskId>,
    val triggers: MutableList<TriggerId>
) {
    fun toModel(): JobDefinition {
        return JobDefinition(
            id = this.id,
            reference = this.reference,
            tasks = this.tasks.map(MemoryJobDefinitionRepository::getTask),
            triggers = this.triggers.map(MemoryJobDefinitionRepository::getTrigger)
        )
    }
}


internal data class TaskEntity(
    val id: TaskId,
    val jobDefinitionId: JobDefinitionId,
    val type: Task.Type
) {
    fun toModel(): Task {
        return ScriptTask(id)
    }
}

internal data class TriggerEntity(
    val id: TriggerId,
    val jobDefinitionId: JobDefinitionId,
    var reference: TriggerReference
) {
    fun toModel(): Trigger {
        return Trigger.ManualTrigger(
            id = this.id,
            reference = this.reference,
            jobDefinitionId = this.jobDefinitionId
        )
    }
}