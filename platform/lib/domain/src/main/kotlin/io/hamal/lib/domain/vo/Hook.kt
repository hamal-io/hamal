package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectHotObject
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId

class HookId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

class HookName(override val value: String) : ValueObjectString()

class HookHeaders(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()

class HookParameters(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()

class HookContent(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()