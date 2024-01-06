package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.base.MapValueObject
import io.hamal.lib.kua.type.KuaMap

class FlowId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    companion object {
        val root = FlowId(1)
    }
}


class FlowName(override val value: String) : ValueObjectString() {
    companion object {
        val default = FlowName("__default__")
    }
}

class FlowInputs(override val value: KuaMap = KuaMap()) : MapValueObject()

class FlowType(override val value: String) : ValueObjectString() {
    companion object {
        val default = FlowType("__default__")
    }
}