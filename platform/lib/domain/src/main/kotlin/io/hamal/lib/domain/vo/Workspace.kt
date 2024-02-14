package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.snowflake.SnowflakeId

class WorkspaceId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    companion object {
        val root = WorkspaceId(1)
    }
}

class WorkspaceName(override val value: String) : ValueObjectString()