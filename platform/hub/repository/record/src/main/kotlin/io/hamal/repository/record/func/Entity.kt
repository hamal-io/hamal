package io.hamal.repository.record.func

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.CodeType
import io.hamal.repository.api.Func
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence

data class Entity(
    override val cmdId: CmdId,
    override val id: FuncId,
    val groupId: GroupId,
    override val sequence: RecordSequence,

    var namespaceId: NamespaceId? = null,
    var name: FuncName? = null,
    var inputs: FuncInputs? = null,
    var code: CodeType? = null

) : RecordEntity<FuncId, FuncRecord, Func> {

    override fun apply(rec: FuncRecord): Entity {
        return when (rec) {
            is FuncCreationRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                namespaceId = rec.namespaceId,
                name = rec.name,
                inputs = rec.inputs,
                code = rec.code
            )

            is FuncUpdatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                namespaceId = rec.namespaceId,
                name = rec.name,
                inputs = rec.inputs,
                code = rec.code
            )
        }
    }

    override fun toDomainObject(): Func {
        return Func(
            cmdId = cmdId,
            id = id,
            groupId = groupId,
            namespaceId = namespaceId!!,
            name = name!!,
            inputs = inputs!!,
            code = code!!,
        )
    }
}

fun List<FuncRecord>.createEntity(): Entity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is FuncCreationRecord)

    var result = Entity(
        id = firstRecord.entityId,
        groupId = firstRecord.groupId,
        cmdId = firstRecord.cmdId,
        sequence = firstRecord.sequence()
    )

    forEach { record ->
        result = result.apply(record)
    }

    return result
}