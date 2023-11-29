package io.hamal.repository.sqlite.record.func

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository
import io.hamal.repository.api.FuncCmdRepository.CreateCmd
import io.hamal.repository.api.FuncCmdRepository.UpdateCmd
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.FuncRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.func.*
import io.hamal.repository.sqlite.record.SqliteRecordRepository
import java.nio.file.Path

internal object CreateFunc : CreateDomainObject<FuncId, FuncRecord, Func> {
    override fun invoke(recs: List<FuncRecord>): Func {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is FuncCreatedRecord)

        var result = FuncEntity(
            cmdId = firstRecord.cmdId,
            id = firstRecord.entityId,
            groupId = firstRecord.groupId,
            sequence = firstRecord.sequence(),
            recordedAt = firstRecord.recordedAt()
        )

        recs.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class FuncSqliteRepository(
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
    ) : SqliteBaseRepository.Config {
        override val filename = "func.db"
    }

    override fun create(cmd: CreateCmd): Func {
        val funcId = cmd.funcId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, funcId)) {
                versionOf(funcId, cmdId)
            } else {
                store(
                    FuncCreatedRecord(
                        cmdId = cmdId,
                        entityId = funcId,
                        groupId = cmd.groupId,
                        flowId = cmd.flowId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        codeId = cmd.codeId,
                        codeVersion = cmd.codeVersion
                    )
                )

                currentVersion(funcId)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }

    override fun deploy(funcId: FuncId, cmd: FuncCmdRepository.DeployCmd): Func {
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, funcId)) {
                versionOf(funcId, cmdId)
            } else {
                val current = versionOf(funcId, cmdId)
                require(cmd.versionToDeploy <= current.code.version) { "${cmd.versionToDeploy} can not be deployed" }
                store(
                    FuncDeployedRecord(
                        cmdId = cmdId,
                        entityId = funcId,
                        deployedVersion = cmd.versionToDeploy
                    )
                )
                currentVersion(funcId)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }

    override fun deployLatest(funcId: FuncId, cmd: CmdId): Func {
        return tx {
            if (commandAlreadyApplied(cmd, funcId)) {
                versionOf(funcId, cmd)
            } else {
                val last = lastRecordOf(funcId)
                store(
                    FuncDeployedRecord(
                        entityId = funcId,
                        cmdId = cmd,
                        deployedVersion = versionOf(funcId, last.sequence())!!.code.version
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
            if (commandAlreadyApplied(cmdId, funcId)) {
                versionOf(funcId, cmdId)
            } else {
                val currentVersion = versionOf(funcId, cmdId)
                store(
                    FuncUpdatedRecord(
                        entityId = funcId,
                        cmdId = cmdId,
                        name = cmd.name ?: currentVersion.name,
                        inputs = cmd.inputs ?: currentVersion.inputs,
                        codeVersion = cmd.codeVersion ?: currentVersion.code.version
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
        return ProjectionCurrent.list(connection, query)
    }

    override fun count(query: FuncQuery): ULong {
        return ProjectionCurrent.count(connection, query)
    }
}