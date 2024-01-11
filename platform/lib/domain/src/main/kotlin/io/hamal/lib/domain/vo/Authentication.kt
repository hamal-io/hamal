package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectInstant
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.snowflake.SnowflakeId
import java.time.Instant

class AuthId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
}

class AuthToken(override val value: String) : ValueObjectString()

class AuthTokenExpiresAt(override val value: Instant) : ValueObjectInstant()

class Password(override val value: String) : ValueObjectString()

class PasswordHash(override val value: String) : ValueObjectString()

class PasswordSalt(override val value: String) : ValueObjectString()
