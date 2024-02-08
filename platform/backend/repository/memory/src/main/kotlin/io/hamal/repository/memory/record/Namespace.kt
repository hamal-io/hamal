package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.FlowName
import io.hamal.repository.api.Flow
import io.hamal.repository.api.FlowCmdRepository.CreateCmd
import io.hamal.repository.api.FlowCmdRepository.UpdateCmd
import io.hamal.repository.api.FlowQueryRepository.FlowQuery
import io.hamal.repository.api.FlowRepository
import io.hamal.repository.record.namespace.CreateFlowFromRecords
import io.hamal.repository.record.namespace.FlowCreatedRecord
import io.hamal.repository.record.namespace.FlowRecord
import io.hamal.repository.record.namespace.FlowUpdatedRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private object FlowCurrentProjection {
    private val projection = mutableMapOf<FlowId, Flow>()

    fun apply(flow: Flow) {
        val currentFlow = projection[flow.id]
        projection.remove(flow.id)

        val flowsInGroup = projection.values.filter { it.groupId == flow.groupId }
        if (flowsInGroup.any { it.name == flow.name }) {
            if (currentFlow != null) {
                projection[currentFlow.id] = currentFlow
            }
            throw IllegalArgumentException("${flow.name} already exists")
        }

        projection[flow.id] = flow
    }

    fun find(flowId: FlowId): Flow? = projection[flowId]
    fun find(flowName: FlowName): Flow? = projection.values.find { it.name == flowName }

    fun list(query: FlowQuery): List<Flow> {
        return projection.filter { query.flowIds.isEmpty() || it.key in query.flowIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter {
                if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId)
            }.dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: FlowQuery): Count {
        return Count(
            projection.filter { query.flowIds.isEmpty() || it.key in query.flowIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .filter {
                    if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId)
                }.dropWhile { it.id >= query.afterId }
                .count()
                .toLong()
        )
    }

    fun clear() {
        projection.clear()
    }
}

class FlowMemoryRepository : RecordMemoryRepository<FlowId, FlowRecord, Flow>(
    createDomainObject = CreateFlowFromRecords,
    recordClass = FlowRecord::class
), FlowRepository {

    override fun create(cmd: CreateCmd): Flow {
        return lock.withLock {
            val flowId = cmd.flowId
            if (commandAlreadyApplied(cmd.id, flowId)) {
                versionOf(flowId, cmd.id)
            } else {
                store(
                    FlowCreatedRecord(
                        cmdId = cmd.id,
                        entityId = flowId,
                        groupId = cmd.groupId,
                        type = cmd.type!!,
                        name = cmd.name,
                        inputs = cmd.inputs,
                    )
                )
                (currentVersion(flowId)).also(FlowCurrentProjection::apply)
            }
        }
    }

    override fun update(flowId: FlowId, cmd: UpdateCmd): Flow {
        return lock.withLock {
            if (commandAlreadyApplied(cmd.id, flowId)) {
                versionOf(flowId, cmd.id)
            } else {
                val current = currentVersion(flowId)
                store(
                    FlowUpdatedRecord(
                        entityId = flowId,
                        cmdId = cmd.id,
                        name = cmd.name ?: current.name,
                        inputs = cmd.inputs ?: current.inputs,
                    )
                )
                (currentVersion(flowId)).also(FlowCurrentProjection::apply)
            }
        }
    }

    override fun find(flowId: FlowId): Flow? =
        lock.withLock { FlowCurrentProjection.find(flowId) }

    override fun find(flowName: FlowName): Flow? =
        lock.withLock { FlowCurrentProjection.find(flowName) }

    override fun list(query: FlowQuery): List<Flow> = lock.withLock { FlowCurrentProjection.list(query) }

    override fun count(query: FlowQuery): Count = lock.withLock { FlowCurrentProjection.count(query) }

    override fun clear() {
        lock.withLock {
            super.clear()
            FlowCurrentProjection.clear()
        }
    }

    override fun close() {}

    private val lock = ReentrantLock()
}
