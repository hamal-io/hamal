package io.hamal.repository.sqlite.record.hook

import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.Hook
import io.hamal.repository.api.HookCmdRepository.CreateCmd
import io.hamal.repository.api.HookCmdRepository.UpdateCmd
import io.hamal.repository.api.HookQueryRepository.HookQuery
import io.hamal.repository.api.HookRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.hook.HookCreatedRecord
import io.hamal.repository.record.hook.HookEntity
import io.hamal.repository.record.hook.HookRecord
import io.hamal.repository.record.hook.HookUpdatedRecord
import io.hamal.repository.sqlite.record.SqliteRecordRepository
import java.nio.file.Path

internal object CreateHook : CreateDomainObject<HookId, HookRecord, Hook> {
    override fun invoke(recs: List<HookRecord>): Hook {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is HookCreatedRecord)

        var result = HookEntity(
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

class SqliteHookRepository(
    config: Config
) : SqliteRecordRepository<HookId, HookRecord, Hook>(
    config = config,
    createDomainObject = CreateHook,
    recordClass = HookRecord::class,
    projections = listOf(
        ProjectionCurrent,
        ProjectionUniqueName
    )
), HookRepository {

    data class Config(
        override val path: Path
    ) : SqliteBaseRepository.Config {
        override val filename = "hook.db"
    }

    override fun create(cmd: CreateCmd): Hook {
        val hookId = cmd.hookId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, hookId)) {
                versionOf(hookId, cmdId)
            } else {
                store(
                    HookCreatedRecord(
                        cmdId = cmdId,
                        entityId = hookId,
                        groupId = cmd.groupId,
                        namespaceId = cmd.namespaceId,
                        name = cmd.name
                    )
                )

                currentVersion(hookId)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }

    override fun update(hookId: HookId, cmd: UpdateCmd): Hook {
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, hookId)) {
                versionOf(hookId, cmdId)
            } else {
                val currentVersion = versionOf(hookId, cmdId)
                store(
                    HookUpdatedRecord(
                        entityId = hookId,
                        cmdId = cmdId,
                        name = cmd.name ?: currentVersion.name
                    )
                )
                currentVersion(hookId)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }

    override fun find(hookId: HookId): Hook? {
        return ProjectionCurrent.find(connection, hookId)
    }

    override fun list(query: HookQuery): List<Hook> {
        return ProjectionCurrent.list(connection, query)
    }

    override fun count(query: HookQuery): ULong {
        return ProjectionCurrent.count(connection, query)
    }
}