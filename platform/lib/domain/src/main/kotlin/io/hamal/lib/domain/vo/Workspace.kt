package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableString

class WorkspaceId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    companion object {
        val root = WorkspaceId(1337)
    }
}

class WorkspaceName(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun WorkspaceName(value: String) = WorkspaceName(ValueString(value))
    }
}