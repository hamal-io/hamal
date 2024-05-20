package io.hamal.lib.domain.vo

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableSnowflakeId
import io.hamal.lib.common.value.ValueVariableString

class ExtensionId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun ExtensionId(value: SnowflakeId) = ExtensionId(ValueSnowflakeId(value))
        fun ExtensionId(value: Int) = ExtensionId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun ExtensionId(value: String) = ExtensionId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}


class ExtensionName(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun ExtensionName(value: String) = ExtensionName(ValueString(value))
    }
}


class ExtensionFile(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun ExtensionFileName(value: String) = ExtensionFile(ValueString(value))
    }
}
