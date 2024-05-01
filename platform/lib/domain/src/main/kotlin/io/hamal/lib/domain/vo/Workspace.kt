package io.hamal.lib.domain.vo

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableSnowflakeId
import io.hamal.lib.common.value.ValueVariableString

class WorkspaceId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        val root = WorkspaceId(1337)

        fun WorkspaceId(value: SnowflakeId) = WorkspaceId(ValueSnowflakeId(value))
        fun WorkspaceId(value: Int) = WorkspaceId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun WorkspaceId(value: String) = WorkspaceId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}

class WorkspaceName(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun WorkspaceName(value: String) = WorkspaceName(ValueString(value))
    }
}