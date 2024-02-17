package io.hamal.repository.sqlite.record.namespace

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceCmdRepository
import io.hamal.repository.api.NamespaceCmdRepository.CreateCmd
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import io.hamal.repository.api.NamespaceRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.namespace.NamespaceEntity
import io.hamal.repository.record.namespace.NamespaceRecord
import io.hamal.repository.sqlite.record.RecordSqliteRepository
import java.nio.file.Path

internal object CreateNamespace : CreateDomainObject<NamespaceId, NamespaceRecord, Namespace> {
    override fun invoke(recs: List<NamespaceRecord>): Namespace {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is NamespaceRecord.Created)

        var result = NamespaceEntity(
            cmdId = firstRecord.cmdId,
            id = firstRecord.entityId,
            parentId = firstRecord.parentId,
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

class NamespaceSqliteRepository(
    path: Path
) : RecordSqliteRepository<NamespaceId, NamespaceRecord, Namespace>(
    path = path,
    filename = "namespace.db",
    createDomainObject = CreateNamespace,
    recordClass = NamespaceRecord::class,
    projections = listOf(ProjectionCurrent, ProjectionUniqueName)
), NamespaceRepository {

    override fun create(cmd: CreateCmd): Namespace {
        val namespaceId = cmd.namespaceId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, namespaceId)) {
                versionOf(namespaceId, cmdId)
            } else {
                store(
                    NamespaceRecord.Created(
                        cmdId = cmdId,
                        entityId = namespaceId,
                        parentId = cmd.parentId,
                        workspaceId = cmd.workspaceId,
                        name = cmd.name
                    )
                )

                currentVersion(namespaceId)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }

    override fun update(namespaceId: NamespaceId, cmd: NamespaceCmdRepository.UpdateCmd): Namespace {
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, namespaceId)) {
                versionOf(namespaceId, cmdId)
            } else {
                val current = currentVersion(namespaceId)
                store(
                    NamespaceRecord.Updated(
                        entityId = namespaceId,
                        cmdId = cmdId,
                        name = cmd.name ?: current.name
                    )
                )
                currentVersion(namespaceId)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }

    override fun find(namespaceId: NamespaceId): Namespace? {
        return ProjectionCurrent.find(connection, namespaceId)
    }

    override fun find(namespaceName: NamespaceName): Namespace? {
        return ProjectionUniqueName.find(connection, namespaceName)?.let { find((it)) }
    }

    override fun list(query: NamespaceQuery): List<Namespace> {
        return ProjectionCurrent.list(connection, query)
    }

    override fun count(query: NamespaceQuery): Count {
        return ProjectionCurrent.count(connection, query)
    }
}