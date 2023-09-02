package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.domain.DomainIdSerializer
import io.hamal.lib.common.domain.ValueObject
import kotlinx.serialization.Serializable

@Serializable(with = AuthenticationId.Serializer::class)
class AuthenticationId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<AuthenticationId>(::AuthenticationId)
}

@Serializable
class AuthenticationPasswordHash(override val value: String) : ValueObject.ComparableImpl<String>()


