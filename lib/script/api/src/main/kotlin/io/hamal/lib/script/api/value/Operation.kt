package io.hamal.lib.script.api.value

sealed interface ValueOperation {
    val operationType: Type

    enum class Type(identifier: String) {
        Add("__add"),
        Sub("__sub")
    }
}

interface PrefixValueOperation : ValueOperation {
    val selfType: String
    operator fun invoke(self: Value): Value
}

interface InfixValueOperation : ValueOperation {
    val selfType: String
    val otherType: String
    operator fun invoke(self: Value, other: Value): Value
}
