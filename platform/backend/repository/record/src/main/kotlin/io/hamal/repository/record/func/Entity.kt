package io.hamal.repository.record.func

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncCode
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence

data class FuncEntity(
    override val cmdId: CmdId,
    override val id: FuncId,
    val groupId: GroupId,
    override val sequence: RecordSequence,

    var flowId: FlowId? = null,
    var name: FuncName? = null,
    var inputs: FuncInputs? = null,
    var code: FuncCode? = null,

    ) : RecordEntity<FuncId, FuncRecord, Func> {

    override fun apply(rec: FuncRecord): FuncEntity {
        return when (rec) {
            is FuncCreatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                flowId = rec.flowId,
                name = rec.name,
                inputs = rec.inputs,
                code = FuncCode(
                    id = rec.codeId,
                    version = rec.codeVersion,
                    deployedVersion = rec.codeVersion
                )
            )

            is FuncUpdatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                inputs = rec.inputs,
                code = FuncCode(
                    id = code!!.id,
                    version = rec.codeVersion,
                    deployedVersion = code!!.deployedVersion
                )
            )

            is FuncDeployedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = name,
                inputs = inputs,
                code = FuncCode(
                    id = code!!.id,
                    version = code!!.version,
                    deployedVersion = rec.deployedVersion
                )
            )
        }
    }

    override fun toDomainObject(): Func {
        return Func(
            cmdId = cmdId,
            id = id,
            groupId = groupId,
            flowId = flowId!!,
            name = name!!,
            inputs = inputs!!,
            code = code!!,
        )
    }
}

fun List<FuncRecord>.createEntity(): FuncEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is FuncCreatedRecord)

    var result = FuncEntity(
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

object CreateFuncFromRecords : CreateDomainObject<FuncId, FuncRecord, Func> {
    override fun invoke(recs: List<FuncRecord>): Func {
        return recs.createEntity().toDomainObject()
    }
}