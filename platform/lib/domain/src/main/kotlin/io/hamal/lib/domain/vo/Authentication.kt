package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.snowflake.SnowflakeId

class AuthId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    companion object {
        val anonymous = AuthId(1)
        val runner = AuthId(2)
        val system = AuthId(42)
        val root = AuthId(1337)
    }
}

class AuthToken(override val value: String) : ValueObjectString()

class Password(override val value: String) : ValueObjectString()

class PasswordHash(override val value: String) : ValueObjectString()

class PasswordSalt(override val value: String) : ValueObjectString()
