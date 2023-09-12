package io.hamal.repository.sqlite.record.func

import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sqlite.BaseSqliteRepository
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository.CreateCmd
import io.hamal.repository.api.FuncCmdRepository.UpdateCmd
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.FuncRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.func.Entity
import io.hamal.repository.record.func.FuncCreationRecord
import io.hamal.repository.record.func.FuncRecord
import io.hamal.repository.record.func.FuncUpdatedRecord
import io.hamal.repository.sqlite.record.SqliteRecordRepository
import java.nio.file.Path

internal object CreateFunc : CreateDomainObject<FuncId, FuncRecord, Func> {
    override fun invoke(recs: List<FuncRecord>): Func {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is FuncCreationRecord)

        var result = Entity(
            cmdId = firstRecord.cmdId,
            id = firstRecord.entityId,
            groupId = firstRecord.groupId,
            sequence = firstRecord.sequence()
        )

        recs.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class SqliteFuncRepository(
    config: Config
) : SqliteRecordRepository<FuncId, FuncRecord, Func>(
    config = config,
    createDomainObject = CreateFunc,
    recordClass = FuncRecord::class,
    projections = listOf(
        ProjectionCurrent,
        ProjectionUniqueName
    )
), FuncRepository {

    data class Config(
        override val path: Path
    ) : BaseSqliteRepository.Config {
        override val filename = "func.db"
    }

    override fun create(cmd: CreateCmd): Func {
        val funcId = cmd.funcId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(funcId, cmdId)) {
                versionOf(funcId, cmdId)
            } else {
                storeRecord(
                    FuncCreationRecord(
                        cmdId = cmdId,
                        entityId = funcId,
                        groupId = cmd.groupId,
                        namespaceId = cmd.namespaceId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        code = cmd.code,
                    )
                )

                currentVersion(funcId)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }

    override fun update(funcId: FuncId, cmd: UpdateCmd): Func {
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(funcId, cmdId)) {
                versionOf(funcId, cmdId)
            } else {
                val currentVersion = versionOf(funcId, cmdId)
                storeRecord(
                    FuncUpdatedRecord(
                        entityId = funcId,
                        cmdId = cmdId,
                        namespaceId = cmd.namespaceId ?: currentVersion.namespaceId,
                        name = cmd.name ?: currentVersion.name,
                        inputs = cmd.inputs ?: currentVersion.inputs,
                        code = cmd.code ?: currentVersion.code,
                    )
                )
                currentVersion(funcId)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }

    override fun find(funcId: FuncId): Func? {
        return ProjectionCurrent.find(connection, funcId)
    }

    override fun list(query: FuncQuery): List<Func> {
        return ProjectionCurrent.list(connection, query.afterId, query.limit)
    }
}