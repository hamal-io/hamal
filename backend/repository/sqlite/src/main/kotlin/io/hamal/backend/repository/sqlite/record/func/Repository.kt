package io.hamal.backend.repository.sqlite.record.func

import io.hamal.backend.repository.api.FuncCmdRepository
import io.hamal.backend.repository.api.FuncCmdRepository.CreateCmd
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.repository.record.CreateDomainObject
import io.hamal.backend.repository.record.func.Entity
import io.hamal.backend.repository.record.func.FuncCreationRecord
import io.hamal.backend.repository.record.func.FuncRecord
import io.hamal.backend.repository.sqlite.BaseRepository
import io.hamal.backend.repository.sqlite.record.SqliteRecordRepository
import io.hamal.lib.domain.Func
import io.hamal.lib.domain.vo.FuncId
import java.nio.file.Path

internal object CreateFunc : CreateDomainObject<FuncId, FuncRecord, Func> {
    override fun invoke(recs: List<FuncRecord>): Func {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is FuncCreationRecord)

        var result = Entity(
            id = firstRecord.entityId,
            cmdId = firstRecord.cmdId,
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
        ProjectionCurrent
    )
), FuncCmdRepository, FuncQueryRepository {

    data class Config(
        override val path: Path
    ) : BaseRepository.Config {
        override val filename = "func"
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
                        entityId = funcId,
                        cmdId = cmdId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        secrets = cmd.secrets,
                        code = cmd.code,
                    )
                )

                currentVersion(funcId)
                    .also { ProjectionCurrent.update(this, it) }
            }
        }
    }

    override fun find(funcId: FuncId): Func? {
        return ProjectionCurrent.find(connection, funcId)
    }

    override fun list(afterId: FuncId, limit: Int): List<Func> {
        return ProjectionCurrent.list(connection, afterId, limit)
    }
}