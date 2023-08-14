package io.hamal.backend.repository.record.func

import io.hamal.backend.repository.api.Func
import io.hamal.backend.repository.record.RecordEntity
import io.hamal.backend.repository.record.RecordSequence
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.type.CodeType

data class Entity(
    override val id: FuncId,
    override val cmdId: CmdId,
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
        cmdId = firstRecord.cmdId,
        sequence = firstRecord.sequence()
    )

    forEach { record ->
        result = result.apply(record)
    }

    return result
}