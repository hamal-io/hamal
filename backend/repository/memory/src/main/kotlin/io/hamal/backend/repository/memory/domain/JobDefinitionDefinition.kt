package io.hamal.backend.repository.memory.domain

import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.backend.core.trigger.Trigger
import io.hamal.backend.repository.api.JobDefinitionRepository
import io.hamal.backend.repository.api.JobDefinitionRepository.Command
import io.hamal.backend.repository.api.JobDefinitionRepository.Command.JobDefinitionToCreate
import io.hamal.backend.repository.api.JobDefinitionRepository.Command.ManualTriggerToCreate
import io.hamal.lib.core.RequestId
import io.hamal.lib.core.vo.JobDefinitionId
import io.hamal.lib.core.vo.JobReference
import io.hamal.lib.core.vo.TriggerId
import io.hamal.lib.core.vo.TriggerReference

object MemoryJobDefinitionRepository : JobDefinitionRepository {

    internal val jobDefinitions = mutableMapOf<JobDefinitionId, JobDefinitionEntity>()
    internal val triggers = mutableMapOf<TriggerId, TriggerEntity>()

    internal val requestIds = mutableSetOf<RequestId>()

    override fun get(id: JobDefinitionId): JobDefinition {
        return jobDefinitions[id]?.let(JobDefinitionEntity::toModel)
            ?: throw IllegalArgumentException("No job definition found with $id")
    }

    override fun getTrigger(id: TriggerId): Trigger {
        return triggers[id]?.let(TriggerEntity::toModel)
            ?: throw IllegalArgumentException("No trigger found with $id")
    }

    override fun execute(requestId: RequestId, commands: List<Command>): List<JobDefinition> {
        check(requestIds.add(requestId)) { "Request $requestId was already executed" }
        val groupedCommands = commands.groupBy { it.jobDefinitionId }
        groupedCommands.forEach { id, cmds ->
            cmds.sortedBy { it.order }.forEach { cmd ->
                when (cmd) {
                    is JobDefinitionToCreate -> createJobDefinition(cmd)
                    is ManualTriggerToCreate -> createManualTrigger(cmd)
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

internal data class JobDefinitionEntity(
    val id: JobDefinitionId,
    val reference: JobReference,
    val triggers: MutableList<TriggerId>
) {
    fun toModel(): JobDefinition {
        return JobDefinition(
            id = this.id,
            reference = this.reference,
            triggers = this.triggers.map(MemoryJobDefinitionRepository::getTrigger)
        )
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