package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.DeployedAt
import io.hamal.lib.domain.vo.FuncId
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository.*
import io.hamal.repository.api.FuncDeployment
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.FuncRepository
import io.hamal.repository.record.func.CreateFuncFromRecords
import io.hamal.repository.record.func.FuncRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private object FuncCurrentProjection {
    private val projection = mutableMapOf<FuncId, Func>()
    fun apply(func: Func) {
        val currentFunc = projection[func.id]
        projection.remove(func.id)

        val funcsInNamespace = projection.values.filter { it.namespaceId == func.namespaceId }
        if (funcsInNamespace.any { it.name == func.name }) {
            if (currentFunc != null) {
                projection[currentFunc.id] = currentFunc
            }
            throw IllegalArgumentException("${func.name} already exists in namespace ${func.namespaceId}")
        }

        projection[func.id] = func
    }

    fun find(funcId: FuncId): Func? = projection[funcId]

    fun list(query: FuncQuery): List<Func> {
        return projection.filter { query.funcIds.isEmpty() || it.key in query.funcIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId) }
            .filter { if (query.namespaceIds.isEmpty()) true else query.namespaceIds.contains(it.namespaceId) }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: FuncQuery): Count {
        return Count(
            projection.filter { query.funcIds.isEmpty() || it.key in query.funcIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .filter { if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId) }
                .filter { if (query.namespaceIds.isEmpty()) true else query.namespaceIds.contains(it.namespaceId) }
                .dropWhile { it.id >= query.afterId }
                .count()
                .toLong()
        )
    }

    fun clear() {
        projection.clear()
    }
}

class FuncMemoryRepository : RecordMemoryRepository<FuncId, FuncRecord, Func>(
    createDomainObject = CreateFuncFromRecords,
    recordClass = FuncRecord::class
), FuncRepository {
    private val lock = ReentrantLock()
    override fun create(cmd: CreateCmd): Func {
        return lock.withLock {
            val funcId = cmd.funcId
            val cmdId = cmd.id
            if (commandAlreadyApplied(cmdId, funcId)) {
                versionOf(funcId, cmd.id)
            } else {
                store(
                    FuncRecord.Created(
                        cmdId = cmd.id,
                        entityId = funcId,
                        workspaceId = cmd.workspaceId,
                        namespaceId = cmd.namespaceId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        codeId = cmd.codeId,
                        codeVersion = cmd.codeVersion
                    )
                )
                (currentVersion(funcId)).also(FuncCurrentProjection::apply)
            }
        }
    }

    override fun deploy(funcId: FuncId, cmd: DeployCmd): Func {
        return lock.withLock {
            if (commandAlreadyApplied(cmd.id, funcId)) {
                versionOf(funcId, cmd.id)
            } else {
                val current = versionOf(funcId, cmd.id)
                require(cmd.version <= current.code.version) { "${cmd.version} can not be deployed" }
                store(
                    FuncRecord.Deployed(
                        cmdId = cmd.id,
                        entityId = funcId,
                        version = cmd.version,
                        message = cmd.message
                    )
                )
                (currentVersion(funcId)).also(FuncCurrentProjection::apply)
            }
        }
    }

    override fun update(funcId: FuncId, cmd: UpdateCmd): Func {
        return lock.withLock {
            if (commandAlreadyApplied(cmd.id, funcId)) {
                versionOf(funcId, cmd.id)
            } else {
                val currentVersion = versionOf(funcId, cmd.id)
                store(
                    FuncRecord.Updated(
                        entityId = funcId,
                        cmdId = cmd.id,
                        name = cmd.name ?: currentVersion.name,
                        inputs = cmd.inputs ?: currentVersion.inputs,
                        codeVersion = cmd.codeVersion ?: currentVersion.code.version
                    )
                )
                (currentVersion(funcId)).also(FuncCurrentProjection::apply)
            }
        }
    }

    override fun find(funcId: FuncId): Func? = lock.withLock { FuncCurrentProjection.find(funcId) }

    override fun list(query: FuncQuery): List<Func> = lock.withLock { FuncCurrentProjection.list(query) }

    override fun listDeployments(funcId: FuncId): List<FuncDeployment> {
        lock.withLock {
            val recs = recordsOf(funcId)
            return recs.filterIsInstance<FuncRecord.Deployed>().map { rec ->
                FuncDeployment(
                    id = CodeId(rec.entityId.value),
                    message = rec.message,
                    version = rec.version,
                    deployedAt = DeployedAt(rec.recordedAt!!.value)
                )
            }
        }
    }

    override fun count(query: FuncQuery): Count = lock.withLock { FuncCurrentProjection.count(query) }

    override fun clear() {
        lock.withLock {
            super.clear()
            FuncCurrentProjection.clear()
        }
    }

    override fun close() {}
}
