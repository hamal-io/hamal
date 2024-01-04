package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectInstant
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.base.MapValueObject
import io.hamal.lib.kua.type.MapType
import java.time.Instant

class FuncId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}


class FuncName(override val value: String) : ValueObjectString()

class FuncInputs(override val value: MapType = MapType()) : MapValueObject()

class DeployMessage(override val value: String) : ValueObjectString() {
    companion object {
        val empty = DeployMessage("")
    }
}

class DeployedAt(override val value: Instant) : ValueObjectInstant() {
    companion object {
        @JvmStatic
        fun now(): DeployedAt = DeployedAt(TimeUtils.now())
    }
}
