package io.hamal.lib.domain.vo

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableSnowflakeId
import io.hamal.lib.common.value.ValueVariableString

class AccountId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        val root = AccountId(1337)
        
        fun AccountId(value: SnowflakeId) = AccountId(ValueSnowflakeId(value))
        fun AccountId(value: Int) = AccountId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun AccountId(value: String) = AccountId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
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