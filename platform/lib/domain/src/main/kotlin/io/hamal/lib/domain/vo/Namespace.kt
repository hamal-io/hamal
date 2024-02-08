package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectHotObject
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId

class NamespaceId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    companion object {
        val root = NamespaceId(1)
    }
}


class NamespaceName(override val value: String) : ValueObjectString() {
    companion object {
        val default = NamespaceName("__default__")
    }
}

class NamespaceInputs(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()

class NamespaceType(override val value: String) : ValueObjectString() {
    companion object {
        val default = NamespaceType("__default__")
    }
}