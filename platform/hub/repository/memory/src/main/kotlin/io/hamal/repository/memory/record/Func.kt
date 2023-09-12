package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.util.CollectionUtils.takeWhileInclusive
import io.hamal.lib.domain.vo.FuncId
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.FuncRepository
import io.hamal.repository.record.func.FuncCreationRecord
import io.hamal.repository.record.func.FuncRecord
import io.hamal.repository.record.func.FuncUpdatedRecord
import io.hamal.repository.record.func.createEntity
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal object CurrentFuncProjection {
    private val projection = mutableMapOf<FuncId, Func>()
    fun apply(func: Func) {

        val values = projection.values.groupBy({ it.namespaceId }, { it.name }).toMutableMap()
        values[func.namespaceId] = values[func.namespaceId]?.plus(func.name) ?: listOf(func.name)
        val unique = values.all { it.value.size == it.value.toSet().size }
        check(unique) { "Func name ${func.name} not unique in namespace ${func.namespaceId}" }

        projection[func.id] = func
    }

    fun find(funcId: FuncId): Func? = projection[funcId]

    fun list(afterId: FuncId, limit: Limit): List<Func> {
        return projection.keys.sorted()
            .reversed()
            .dropWhile { it >= afterId }
            .take(limit.value)
            .mapNotNull { find(it) }
    }

    fun clear() {
        projection.clear()
    }
}

object MemoryFuncRepository : BaseRecordRepository<FuncId, FuncRecord>(), FuncRepository {
    private val lock = ReentrantLock()
    override fun create(cmd: FuncCmdRepository.CreateCmd): Func {
        return lock.withLock {
            val funcId = cmd.funcId
            if (contains(funcId)) {
                versionOf(funcId, cmd.id)
            } else {
                addRecord(
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
            if (commandAlreadyApplied(funcId, cmd.id)) {
                versionOf(funcId, cmd.id)
            } else {
                val currentVersion = versionOf(funcId, cmd.id)
                addRecord(
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
        return CurrentFuncProjection.list(query.afterId, query.limit)
    }

    override fun clear() {
        super.clear()
        CurrentFuncProjection.clear()
    }
}

private fun MemoryFuncRepository.currentVersion(id: FuncId): Func {
    return listRecords(id)
        .createEntity()
        .toDomainObject()
}

private fun MemoryFuncRepository.commandAlreadyApplied(id: FuncId, cmdId: CmdId) =
    listRecords(id).any { it.cmdId == cmdId }

private fun MemoryFuncRepository.versionOf(id: FuncId, cmdId: CmdId): Func {
    return listRecords(id).takeWhileInclusive { it.cmdId != cmdId }
        .createEntity()
        .toDomainObject()
}