package io.hamal.backend.repository.record.func

import io.hamal.backend.repository.record.RecordEntity
import io.hamal.backend.repository.record.RecordSequence
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.Func
import io.hamal.lib.domain.vo.*

data class Entity(
    override val id: FuncId,
    override val cmdId: CmdId,
    override val sequence: RecordSequence,

    var name: FuncName? = null,
    var inputs: FuncInputs? = null,
    var secrets: FuncSecrets? = null,
    var code: Code? = null

) : RecordEntity<FuncId, FuncRecord, Func> {

    override fun apply(rec: FuncRecord): Entity {
        return when (rec) {
            is FuncCreationRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                inputs = rec.inputs,
                secrets = rec.secrets,
                code = rec.code
            )
        }
    }

    override fun toDomainObject(): Func {
        return Func(
            cmdId = cmdId,
            id = id,
            name = name!!,
            inputs = inputs!!,
            secrets = secrets!!,
            code = code!!,
        )
    }
}