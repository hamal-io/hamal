package io.hamal.backend.repository.memory.record

import io.hamal.backend.repository.api.FuncCmdRepository
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.backend.repository.record.func.FuncCreationRecord
import io.hamal.backend.repository.record.func.FuncRecord
import io.hamal.backend.repository.record.func.createEntity
import io.hamal.lib.common.util.CollectionUtils.takeWhileInclusive
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.Func
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.Limit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal object CurrentFuncProjection {
    private val projection = mutableMapOf<FuncId, Func>()
    fun apply(func: Func) {
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

object MemoryFuncRepository : BaseRecordRepository<FuncId, FuncRecord>(), FuncCmdRepository, FuncQueryRepository {
    private val lock = ReentrantLock()
    override fun create(cmd: FuncCmdRepository.CreateCmd): Func {
        return lock.withLock {
            val funcId = cmd.funcId
            if (contains(funcId)) {
                versionOf(funcId, cmd.id)
            } else {
                addRecord(
                    FuncCreationRecord(
                        entityId = funcId,
                        cmdId = cmd.id,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        code = cmd.code
                    )
                )
                (currentVersion(funcId)).also(CurrentFuncProjection::apply)
            }
        }
    }

    override fun find(funcId: FuncId): Func? = CurrentFuncProjection.find(funcId)

    override fun list(block: FuncQuery.() -> Unit): List<Func> {
        val query = FuncQuery().also(block)
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