package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableString

class AccountId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    companion object {
        val root = AccountId(1337)
    }
}

class Email(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun Email(value: String) = Email(ValueString(value))
    }
}

class Web3Address(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun Web3Address(value: String) = Web3Address(ValueString(value))
    }
}

class Web3Challenge(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun Web3Challenge(value: String) = Web3Challenge(ValueString(value))
    }
}

class Web3Signature(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun Web3Signature(value: String) = Web3Signature(ValueString(value))
    }
}

enum class AccountType {
    Anonymous,
    Root,
    Runner,
    Service,
    User
}