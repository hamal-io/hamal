package io.hamal.lib.vo

import io.hamal.lib.util.Snowflake
import io.hamal.lib.vo.base.DomainId
import io.hamal.lib.vo.base.DomainIdSerializer
import kotlinx.serialization.Serializable

@Serializable(with = AccountId.Serializer::class)
class AccountId(override val value: Snowflake.Id) : DomainId() {
    constructor(value: Int) : this(Snowflake.Id(value.toLong()))
    internal object Serializer : DomainIdSerializer<AccountId>(::AccountId)
}
