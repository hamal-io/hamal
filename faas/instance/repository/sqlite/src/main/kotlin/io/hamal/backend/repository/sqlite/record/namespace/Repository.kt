package io.hamal.backend.repository.sqlite.record.namespace

import io.hamal.backend.repository.api.Namespace
import io.hamal.backend.repository.api.NamespaceCmdRepository
import io.hamal.backend.repository.api.NamespaceCmdRepository.CreateCmd
import io.hamal.backend.repository.api.NamespaceQueryRepository
import io.hamal.backend.repository.api.NamespaceQueryRepository.NamespaceQuery
import io.hamal.backend.repository.record.CreateDomainObject
import io.hamal.backend.repository.record.namespace.Entity
import io.hamal.backend.repository.record.namespace.NamespaceCreationRecord
import io.hamal.backend.repository.record.namespace.NamespaceRecord
import io.hamal.backend.repository.record.namespace.NamespaceUpdatedRecord
import io.hamal.backend.repository.sqlite.record.SqliteRecordRepository
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sqlite.BaseSqliteRepository
import java.nio.file.Path

internal object CreateNamespace : CreateDomainObject<NamespaceId, NamespaceRecord, Namespace> {
    override fun invoke(recs: List<NamespaceRecord>): Namespace {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is NamespaceCreationRecord)

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

class SqliteNamespaceRepository(
    config: Config
) : SqliteRecordRepository<NamespaceId, NamespaceRecord, Namespace>(
    config = config,
    createDomainObject = CreateNamespace,
    recordClass = NamespaceRecord::class,
    projections = listOf(ProjectionCurrent, ProjectionUniqueName)
), NamespaceCmdRepository, NamespaceQueryRepository {

    data class Config(
        override val path: Path
    ) : BaseSqliteRepository.Config {
        override val filename = "namespace.db"
    }

    override fun create(cmd: CreateCmd): Namespace {
        val namespaceId = cmd.namespaceId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(namespaceId, cmdId)) {
                versionOf(namespaceId, cmdId)
            } else {
                storeRecord(
                    NamespaceCreationRecord(
                        entityId = namespaceId,
                        cmdId = cmdId,
                        name = cmd.name,
                        inputs = cmd.inputs,
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
            if (commandAlreadyApplied(namespaceId, cmdId)) {
                versionOf(namespaceId, cmdId)
            } else {
                storeRecord(
                    NamespaceUpdatedRecord(
                        entityId = namespaceId,
                        cmdId = cmdId,
                        name = cmd.name,
                        inputs = cmd.inputs,
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

    override fun list(block: NamespaceQuery.() -> Unit): List<Namespace> {
        val query = NamespaceQuery().also(block)
        return ProjectionCurrent.list(connection, query.afterId, query.limit)
    }
}