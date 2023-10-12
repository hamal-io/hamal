package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.domain.DomainIdSerializer
import io.hamal.lib.common.domain.StringValueObject
import io.hamal.lib.common.domain.StringValueObjectSerializer
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.base.DomainAt
import io.hamal.lib.domain.vo.base.DomainAtSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable(with = AuthId.Serializer::class)
class AuthId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<AuthId>(::AuthId)
}

@Serializable(with = AuthToken.Serializer::class)
class AuthToken(override val value: String) : StringValueObject() {
    internal object Serializer : StringValueObjectSerializer<AuthToken>(::AuthToken)
}

@Serializable(with = AuthTokenExpiresAt.Serializer::class)
class AuthTokenExpiresAt(override val value: Instant) : DomainAt() {
    internal object Serializer : DomainAtSerializer<AuthTokenExpiresAt>(::AuthTokenExpiresAt)
}

@Serializable(with = Password.Serializer::class)
class Password(override val value: String) : StringValueObject() {
    internal object Serializer : StringValueObjectSerializer<Password>(::Password)
}

@Serializable(with = PasswordHash.Serializer::class)
class PasswordHash(override val value: String) : StringValueObject() {
    internal object Serializer : StringValueObjectSerializer<PasswordHash>(::PasswordHash)
}

@Serializable(with = PasswordSalt.Serializer::class)
class PasswordSalt(override val value: String) : StringValueObject() {
    internal object Serializer : StringValueObjectSerializer<PasswordSalt>(::PasswordSalt)
}
