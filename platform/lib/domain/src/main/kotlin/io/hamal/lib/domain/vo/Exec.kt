package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.base.MapValueObject
import io.hamal.lib.kua.type.MapType

class ExecId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

class ExecInputs(override val value: MapType = MapType()) : MapValueObject()

data class ExecCode(
    val id: CodeId? = null,
    val version: CodeVersion? = null,
    val value: CodeValue? = null,
)

enum class ExecStatus(val value: Int) {
    Planned(1),
    Scheduled(2),
    Queued(3),
    Started(4),
    Completed(5),
    Failed(6);

    companion object {
        fun valueOf(value: Int) = requireNotNull(mapped[value]) { "$value is not an exec status" }

        private val mapped = ExecStatus.values()
            .associateBy { it.value }
    }
}


class ExecToken(override val value: String) : ValueObjectString()

class ExecResult(override val value: MapType = MapType()) : MapValueObject()

class ExecState(override val value: MapType = MapType()) : MapValueObject()

