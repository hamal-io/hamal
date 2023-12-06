package io.hamal.repository.memory.record

import io.hamal.lib.domain.vo.FuncId
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository.*
import io.hamal.repository.api.FuncDeploymentsRes
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.FuncRepository
import io.hamal.repository.record.func.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private object FuncCurrentProjection {
    private val projection = mutableMapOf<FuncId, Func>()
    fun apply(func: Func) {
        val currentFunc = projection[func.id]
        projection.remove(func.id)

        val funcsInFlow = projection.values.filter { it.flowId == func.flowId }
        if (funcsInFlow.any { it.name == func.name }) {
            if (currentFunc != null) {
                projection[currentFunc.id] = currentFunc
            }
            throw IllegalArgumentException("${func.name} already exists in flow ${func.flowId}")
        }

        projection[func.id] = func
    }

    fun find(funcId: FuncId): Func? = projection[funcId]

    fun list(query: FuncQuery): List<Func> {
        return projection.filter { query.funcIds.isEmpty() || it.key in query.funcIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId) }
            .filter { if (query.flowIds.isEmpty()) true else query.flowIds.contains(it.flowId) }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: FuncQuery): ULong {
        return projection.filter { query.funcIds.isEmpty() || it.key in query.funcIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId) }
            .filter { if (query.flowIds.isEmpty()) true else query.flowIds.contains(it.flowId) }
            .dropWhile { it.id >= query.afterId }
            .count()
            .toULong()
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
                    FuncCreatedRecord(
                        cmdId = cmd.id,
                        entityId = funcId,
                        groupId = cmd.groupId,
                        flowId = cmd.flowId,
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
                    FuncDeployedRecord(
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
                    FuncUpdatedRecord(
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

    override fun listDeployments(funcId: FuncId): List<FuncDeploymentsRes> {
        lock.withLock {
            val recs = recordsOf(funcId)//.reversed().take()
            return recs.map { rec ->
                val dep = versionOf(funcId, rec.sequence())!!.deployment
                FuncDeploymentsRes(
                    message = dep.message,
                    version = dep.version,
                    deployedAt = rec.recordedAt()
                )
            }
        }
    }

    override fun count(query: FuncQuery): ULong = lock.withLock { FuncCurrentProjection.count(query) }

    override fun clear() {
        lock.withLock {
            super.clear()
            FuncCurrentProjection.clear()
        }
    }

    override fun close() {}
}
