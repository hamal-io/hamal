package io.hamal.repository.sqlite.record.func

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.DeployedAt
import io.hamal.lib.domain.vo.FuncId
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCmdRepository.*
import io.hamal.repository.api.FuncDeployment
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.FuncRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.func.FuncEntity
import io.hamal.repository.record.func.FuncRecord
import io.hamal.repository.sqlite.record.RecordSqliteRepository
import java.nio.file.Path

internal object CreateFunc : CreateDomainObject<FuncId, FuncRecord, Func> {
    override fun invoke(recs: List<FuncRecord>): Func {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is FuncRecord.Created)

        var result = FuncEntity(
            cmdId = firstRecord.cmdId,
            id = firstRecord.entityId,
            workspaceId = firstRecord.workspaceId,
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
    path: Path
) : RecordSqliteRepository<FuncId, FuncRecord, Func>(
    path = path,
    filename = "func.db",
    createDomainObject = CreateFunc,
    recordClass = FuncRecord::class,
    projections = listOf(
        ProjectionCurrent,
        ProjectionUniqueName
    )
), FuncRepository {

    override fun create(cmd: CreateCmd): Func {
        val funcId = cmd.funcId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, funcId)) {
                versionOf(funcId, cmdId)
            } else {
                store(
                    FuncRecord.Created(
                        cmdId = cmdId,
                        entityId = funcId,
                        workspaceId = cmd.workspaceId,
                        namespaceId = cmd.namespaceId,
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

    override fun deploy(funcId: FuncId, cmd: DeployCmd): Func {
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, funcId)) {
                versionOf(funcId, cmdId)
            } else {
                val current = versionOf(funcId, cmdId)
                require(cmd.version <= current.code.version) { "code version ${cmd.version} can not be deployed" }
                store(
                    FuncRecord.Deployed(
                        cmdId = cmd.id,
                        entityId = funcId,
                        version = cmd.version,
                        message = cmd.message
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
                    FuncRecord.Updated(
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

    override fun listDeployments(funcId: FuncId): List<FuncDeployment> {
        return tx {
            val recs = recordsOf(funcId)
            recs.filterIsInstance<FuncRecord.Deployed>().map { rec ->
                FuncDeployment(
                    id = CodeId(rec.entityId.value),
                    message = rec.message,
                    version = rec.version,
                    deployedAt = DeployedAt(rec.recordedAt!!.value)
                )
            }
        }
    }

    override fun count(query: FuncQuery): Count {
        return ProjectionCurrent.count(connection, query)
    }
}