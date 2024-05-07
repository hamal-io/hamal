package io.hamal.lib.domain.vo

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableSnowflakeId
import io.hamal.lib.common.value.ValueVariableString

class AuthId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {

    companion object {
        val anonymous = AuthId(1)
        val runner = AuthId(2)
        val system = AuthId(42)
        val root = AuthId(1337)

        fun AuthId(value: SnowflakeId) = AuthId(ValueSnowflakeId(value))
        fun AuthId(value: Int) = AuthId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun AuthId(value: String) = AuthId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}

class AuthToken(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun AuthToken(value: String) = AuthToken(ValueString(value))
    }
}

class Password(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun Password(value: String) = Password(ValueString(value))
    }
}

class PasswordHash(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun PasswordHash(value: String) = PasswordHash(ValueString(value))
    }
}

class PasswordSalt(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun PasswordSalt(value: String) = PasswordSalt(ValueString(value))
    }
}
