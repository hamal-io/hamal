package io.hamal.repository.memory.record

import io.hamal.lib.domain.vo.FuncId
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.FuncRepository
import io.hamal.repository.record.func.CreateFuncFromRecords
import io.hamal.repository.record.func.FuncCreationRecord
import io.hamal.repository.record.func.FuncRecord
import io.hamal.repository.record.func.FuncUpdatedRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal object CurrentFuncProjection {
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
            .filter {
                if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId)
            }.dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: FuncQuery): ULong {
        return projection.filter { query.funcIds.isEmpty() || it.key in query.funcIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter {
                if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId)
            }.dropWhile { it.id >= query.afterId }
            .count()
            .toULong()
    }

    fun clear() {
        projection.clear()
    }
}

class MemoryFuncRepository : MemoryRecordRepository<FuncId, FuncRecord, Func>(
    createDomainObject = CreateFuncFromRecords,
    recordClass = FuncRecord::class
), FuncRepository {
    private val lock = ReentrantLock()
    override fun create(cmd: FuncCmdRepository.CreateCmd): Func {
        return lock.withLock {
            val funcId = cmd.funcId
            val cmdId = cmd.id
            if (commandAlreadyApplied(cmdId, funcId)) {
                versionOf(funcId, cmd.id)
            } else {
                store(
                    FuncCreationRecord(
                        cmdId = cmd.id,
                        entityId = funcId,
                        groupId = cmd.groupId,
                        namespaceId = cmd.namespaceId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        code = cmd.code
                    )
                )
                (currentVersion(funcId)).also(CurrentFuncProjection::apply)
            }
        }
    }

    override fun update(funcId: FuncId, cmd: FuncCmdRepository.UpdateCmd): Func {
        return lock.withLock {
            if (commandAlreadyApplied(cmd.id, funcId)) {
                versionOf(funcId, cmd.id)
            } else {
                val currentVersion = versionOf(funcId, cmd.id)
                store(
                    FuncUpdatedRecord(
                        entityId = funcId,
                        cmdId = cmd.id,
                        namespaceId = cmd.namespaceId ?: currentVersion.namespaceId,
                        name = cmd.name ?: currentVersion.name,
                        inputs = cmd.inputs ?: currentVersion.inputs,
                        code = cmd.code ?: currentVersion.code
                    )
                )
                (currentVersion(funcId)).also(CurrentFuncProjection::apply)
            }
        }
    }

    override fun find(funcId: FuncId): Func? = CurrentFuncProjection.find(funcId)

    override fun list(query: FuncQuery): List<Func> {
        return CurrentFuncProjection.list(query)
    }

    override fun count(query: FuncQuery): ULong {
        return CurrentFuncProjection.count(query)
    }

    override fun clear() {
        super.clear()
        CurrentFuncProjection.clear()
    }

    override fun close() {}
}
