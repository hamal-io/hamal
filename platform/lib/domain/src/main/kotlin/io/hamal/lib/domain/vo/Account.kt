package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.snowflake.SnowflakeId

class AccountId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    companion object {
        val root = AccountId(1)
    }
}

class Email(override val value: String) : ValueObjectString()

class Web3Address(override val value: String) : ValueObjectString()

class Web3Challenge(override val value: String) : ValueObjectString()

class Web3Signature(override val value: String) : ValueObjectString()

enum class AccountType {
    Anonymous,
    Root,
    Runner,
    Service,
    User
}